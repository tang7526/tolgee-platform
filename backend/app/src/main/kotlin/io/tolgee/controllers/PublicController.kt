package io.tolgee.controllers

import com.fasterxml.jackson.databind.node.TextNode
import com.sun.istack.NotNull
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.tolgee.component.email.TolgeeEmailSender
import io.tolgee.configuration.tolgee.TolgeeProperties
import io.tolgee.constants.Message
import io.tolgee.dtos.misc.EmailParams
import io.tolgee.dtos.request.auth.ResetPassword
import io.tolgee.dtos.request.auth.ResetPasswordRequest
import io.tolgee.dtos.request.auth.SignUpDto
import io.tolgee.exceptions.BadRequestException
import io.tolgee.exceptions.NotFoundException
import io.tolgee.model.UserAccount
import io.tolgee.security.JwtTokenProviderImpl
import io.tolgee.security.LoginRequest
import io.tolgee.security.payload.JwtAuthenticationResponse
import io.tolgee.security.third_party.GithubOAuthDelegate
import io.tolgee.security.third_party.GoogleOAuthDelegate
import io.tolgee.security.third_party.OAuth2Delegate
import io.tolgee.service.EmailVerificationService
import io.tolgee.service.security.MfaService
import io.tolgee.service.security.ReCaptchaValidationService
import io.tolgee.service.security.SignUpService
import io.tolgee.service.security.UserAccountService
import io.tolgee.service.security.UserCredentialsService
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@RestController
@RequestMapping("/api/public")
@Tag(name = "Authentication")
class PublicController(
  private val tokenProvider: JwtTokenProviderImpl,
  private val githubOAuthDelegate: GithubOAuthDelegate,
  private val googleOAuthDelegate: GoogleOAuthDelegate,
  private val oauth2Delegate: OAuth2Delegate,
  private val properties: TolgeeProperties,
  private val userAccountService: UserAccountService,
  private val tolgeeEmailSender: TolgeeEmailSender,
  private val emailVerificationService: EmailVerificationService,
  private val reCaptchaValidationService: ReCaptchaValidationService,
  private val signUpService: SignUpService,
  private val mfaService: MfaService,
  private val userCredentialsService: UserCredentialsService,
) {
  @Operation(summary = "Generates JWT token")
  @PostMapping("/generatetoken")
  fun authenticateUser(@RequestBody @Valid loginRequest: LoginRequest): ResponseEntity<*> {
    // note: These checks are left to keep the behavior of this legacy endpoint untouched;
    // v2 endpoint will allow for hybrid authentication between platform accounts and ldap accounts
    if (properties.authentication.ldap.enabled && properties.authentication.nativeEnabled) {
      throw RuntimeException("Can not use native auth and ldap auth in the same time")
    }
    if (!properties.authentication.ldap.enabled && !properties.authentication.nativeEnabled) {
      throw RuntimeException("Authentication method not configured")
    }

    val userAccount = userCredentialsService.checkUserCredentials(loginRequest.username, loginRequest.password)
    emailVerificationService.check(userAccount)
    mfaService.checkMfa(userAccount, loginRequest.otp)

    // two factor passed, so we can generate super token
    val jwt = tokenProvider.generateToken(userAccount.id, true).toString()
    return ResponseEntity.ok(JwtAuthenticationResponse(jwt))
  }

  @Operation(summary = "Reset password request")
  @PostMapping("/reset_password_request")
  fun resetPasswordRequest(@RequestBody @Valid request: ResetPasswordRequest) {
    val userAccount = userAccountService.findActive(request.email!!) ?: return
    val code = RandomStringUtils.randomAlphabetic(50)
    userAccountService.setResetPasswordCode(userAccount, code)

    val callbackString = code + "," + request.email
    val url = request.callbackUrl + "/" + Base64.getEncoder().encodeToString(callbackString.toByteArray())
    val isInitial = userAccount.accountType == UserAccount.AccountType.THIRD_PARTY

    val params = EmailParams(
      to = request.email!!,
      subject = if (isInitial) "Initial password configuration" else "Password reset",
      text = """
        Hello! 👋<br/><br/>
        ${if (isInitial) "To set a password for your account, <b>follow this link</b>:<br/>" else "To reset your password, <b>follow this link</b>:<br/>"}
        <a href="$url">$url</a><br/><br/>
        If you have not requested this e-mail, please ignore it.<br/><br/>
        
        Regards,<br/>
        Tolgee<br/><br/>
      """.trimIndent()
    )

    tolgeeEmailSender.sendEmail(params)
  }

  @GetMapping("/reset_password_validate/{email}/{code}")
  @Operation(summary = "Validates key sent by email")
  fun resetPasswordValidate(
    @PathVariable("code") code: String,
    @PathVariable("email") email: String
  ) {
    validateEmailCode(code, email)
  }

  @PostMapping("/reset_password_set")
  @Operation(summary = "Sets new password with password reset code from e-mail")
  fun resetPasswordSet(@RequestBody @Valid request: ResetPassword) {
    val userAccount = validateEmailCode(request.code!!, request.email!!)
    if (userAccount.accountType === UserAccount.AccountType.THIRD_PARTY)
      userAccountService.setAccountType(userAccount, UserAccount.AccountType.LOCAL)
    userAccountService.setUserPassword(userAccount, request.password)
    userAccountService.removeResetCode(userAccount)
  }

  @PostMapping("/sign_up")
  @Transactional
  @Operation(
    summary = """
Creates new user account.

When E-mail verification is enabled, null is returned. Otherwise JWT token is provided.
    """
  )
  fun signUp(@RequestBody @Valid dto: SignUpDto): JwtAuthenticationResponse? {
    if (!reCaptchaValidationService.validate(dto.recaptchaToken, "")) {
      throw BadRequestException(Message.INVALID_RECAPTCHA_TOKEN)
    }
    return signUpService.signUp(dto)
  }

  @GetMapping("/verify_email/{userId}/{code}")
  @Operation(summary = "Sets user account as verified, when code from email is OK")
  fun verifyEmail(
    @PathVariable("userId") @NotNull userId: Long,
    @PathVariable("code") @NotBlank code: String
  ): JwtAuthenticationResponse {
    emailVerificationService.verify(userId, code)
    return JwtAuthenticationResponse(tokenProvider.generateToken(userId).toString())
  }

  @PostMapping(value = ["/validate_email"], consumes = [MediaType.APPLICATION_JSON_VALUE])
  @Operation(summary = "Validates if email is not in use")
  fun validateEmail(@RequestBody email: TextNode): Boolean {
    return userAccountService.findActive(email.asText()) == null
  }

  @GetMapping("/authorize_oauth/{serviceType}")
  @Operation(summary = "Authenticates user using third party oAuth service")
  @Transactional
  fun authenticateUser(
    @PathVariable("serviceType") serviceType: String?,
    @RequestParam(value = "code", required = true) code: String?,
    @RequestParam(value = "redirect_uri", required = true) redirectUri: String?,
    @RequestParam(value = "invitationCode", required = false) invitationCode: String?
  ): JwtAuthenticationResponse {
    if (properties.internal.fakeGithubLogin && code == "this_is_dummy_code") {
      val user = getFakeGithubUser()
      return JwtAuthenticationResponse(tokenProvider.generateToken(user.id).toString())
    }
    return when (serviceType) {
      "github" -> {
        githubOAuthDelegate.getTokenResponse(code, invitationCode)
      }

      "google" -> {
        googleOAuthDelegate.getTokenResponse(code, invitationCode, redirectUri)
      }

      "oauth2" -> {
        oauth2Delegate.getTokenResponse(code, invitationCode, redirectUri)
      }

      else -> {
        throw NotFoundException(Message.SERVICE_NOT_FOUND)
      }
    }
  }

  private fun getFakeGithubUser(): UserAccount {
    val username = "johndoe@doe.com"
    val user = userAccountService.findActive(username) ?: let {
      UserAccount().apply {
        this.username = username
        name = "john"
        accountType = UserAccount.AccountType.THIRD_PARTY
        userAccountService.save(this)
      }
    }
    return user
  }

  private fun validateEmailCode(code: String, email: String): UserAccount {
    val userAccount = userAccountService.findActive(email) ?: throw BadRequestException(Message.BAD_CREDENTIALS)
    val resetCodeValid = userAccountService.isResetCodeValid(userAccount, code)
    if (!resetCodeValid) {
      throw BadRequestException(Message.BAD_CREDENTIALS)
    }
    return userAccount
  }
}
