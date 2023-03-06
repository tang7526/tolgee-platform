package io.tolgee.security.third_party

import com.sun.istack.Nullable
import io.tolgee.configuration.tolgee.GithubAuthenticationProperties
import io.tolgee.configuration.tolgee.TolgeeProperties
import io.tolgee.constants.Message
import io.tolgee.exceptions.AuthenticationException
import io.tolgee.model.Invitation
import io.tolgee.model.UserAccount
import io.tolgee.security.JwtTokenProviderImpl
import io.tolgee.security.payload.JwtAuthenticationResponse
import io.tolgee.service.InvitationService
import io.tolgee.service.security.UserAccountService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*
import java.util.stream.Collectors

@Component
class GithubOAuthDelegate(
  private val tokenProvider: JwtTokenProviderImpl,
  private val userAccountService: UserAccountService,
  private val restTemplate: RestTemplate,
  private val properties: TolgeeProperties,
  private val invitationService: InvitationService
) {
  private val githubConfigurationProperties: GithubAuthenticationProperties = properties.authentication.github

  fun getTokenResponse(receivedCode: String?, @Nullable invitationCode: String?): JwtAuthenticationResponse {
    val body = HashMap<String, String?>()
    body["client_id"] = githubConfigurationProperties.clientId
    body["client_secret"] = githubConfigurationProperties.clientSecret
    body["code"] = receivedCode

    // get token to authorize to github api
    val response: MutableMap<*, *>? = restTemplate
      .postForObject(githubConfigurationProperties.authorizationUrl, body, MutableMap::class.java)
    if (response != null && response.containsKey("access_token")) {
      val headers = HttpHeaders()
      headers["Authorization"] = "token " + response["access_token"]
      val entity = HttpEntity<String?>(null, headers)

      // get github user data
      val exchange = restTemplate
        .exchange(githubConfigurationProperties.userUrl, HttpMethod.GET, entity, GithubUserResponse::class.java)
      if (exchange.statusCode != HttpStatus.OK || exchange.body == null) {
        throw AuthenticationException(Message.THIRD_PARTY_UNAUTHORIZED)
      }
      val userResponse = exchange.body

      // get github user emails
      val emails = restTemplate.exchange(
        githubConfigurationProperties.userUrl + "/emails", HttpMethod.GET, entity,
        Array<GithubEmailResponse>::class.java
      ).body
        ?: throw AuthenticationException(Message.THIRD_PARTY_AUTH_NO_EMAIL)

      val verifiedEmails = Arrays.stream(emails).filter { it.verified }.collect(Collectors.toList())

      val githubEmail = (
        verifiedEmails.firstOrNull { it.primary }
          ?: verifiedEmails.firstOrNull()
        )?.email
        ?: throw AuthenticationException(Message.THIRD_PARTY_AUTH_NO_EMAIL)

      val userAccountOptional = userAccountService.findByThirdParty("github", userResponse!!.id!!)
      val user = userAccountOptional.orElseGet {
        userAccountService.findActive(githubEmail)?.let {
          throw AuthenticationException(Message.USERNAME_ALREADY_EXISTS)
        }

        var invitation: Invitation? = null
        if (invitationCode == null) {
          if (!properties.authentication.registrationsAllowed) {
            throw AuthenticationException(Message.REGISTRATIONS_NOT_ALLOWED)
          }
        } else {
          invitation = invitationService.getInvitation(invitationCode)
        }

        val newUserAccount = UserAccount()
        newUserAccount.username = githubEmail
        newUserAccount.name = userResponse.name ?: userResponse.login
        newUserAccount.thirdPartyAuthId = userResponse.id
        newUserAccount.thirdPartyAuthType = "github"
        newUserAccount.accountType = UserAccount.AccountType.THIRD_PARTY
        userAccountService.createUser(newUserAccount)
        if (invitation != null) {
          invitationService.accept(invitation.code, newUserAccount)
        }
        newUserAccount
      }
      val jwt = tokenProvider.generateToken(user.id).toString()
      return JwtAuthenticationResponse(jwt)
    }
    if (response == null) {
      throw AuthenticationException(Message.THIRD_PARTY_AUTH_UNKNOWN_ERROR)
    }

    if (response.containsKey("error")) {
      throw AuthenticationException(Message.THIRD_PARTY_AUTH_ERROR_MESSAGE)
    }

    throw AuthenticationException(Message.THIRD_PARTY_AUTH_UNKNOWN_ERROR)
  }

  class GithubEmailResponse {
    var email: String? = null
    var primary = false
    var verified = false
  }

  class GithubUserResponse {
    var name: String? = null
    var id: String? = null
    val login: String = ""
  }
}
