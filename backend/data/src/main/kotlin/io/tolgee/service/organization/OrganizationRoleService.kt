package io.tolgee.service.organization

import io.tolgee.dtos.request.organization.SetOrganizationRoleDto
import io.tolgee.dtos.request.validators.exceptions.ValidationException
import io.tolgee.exceptions.NotFoundException
import io.tolgee.exceptions.PermissionException
import io.tolgee.model.Invitation
import io.tolgee.model.Organization
import io.tolgee.model.OrganizationRole
import io.tolgee.model.UserAccount
import io.tolgee.model.enums.OrganizationRoleType
import io.tolgee.repository.OrganizationRepository
import io.tolgee.repository.OrganizationRoleRepository
import io.tolgee.security.AuthenticationFacade
import io.tolgee.service.security.UserAccountService
import io.tolgee.service.security.UserPreferencesService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Service
@Transactional
class OrganizationRoleService(
  private val organizationRoleRepository: OrganizationRoleRepository,
  private val authenticationFacade: AuthenticationFacade,
  private val userAccountService: UserAccountService,
  private val entityManager: EntityManager,
  private val organizationRepository: OrganizationRepository,
  @Lazy
  private val userPreferencesService: UserPreferencesService,
) {

  fun checkUserCanView(organizationId: Long) {
    checkUserCanView(
      authenticationFacade.userAccount.id,
      organizationId,
      authenticationFacade.userAccount.role == UserAccount.Role.ADMIN
    )
  }

  private fun checkUserCanView(userId: Long, organizationId: Long, isAdmin: Boolean = false) {
    if (canUserView(userId, organizationId) || isAdmin) return else throw PermissionException()
  }

  fun canUserView(userId: Long, organizationId: Long) =
    this.organizationRepository.canUserView(userId, organizationId)

  fun checkUserIsOwner(userId: Long, organizationId: Long) {
    val isServerAdmin = userAccountService.get(userId).role == UserAccount.Role.ADMIN
    if (this.isUserOwner(userId, organizationId) || isServerAdmin) return else throw PermissionException()
  }

  fun checkUserIsOwner(organizationId: Long) {
    this.checkUserIsOwner(authenticationFacade.userAccount.id, organizationId)
  }

  fun checkUserIsMemberOrOwner(userId: Long, organizationId: Long) {
    val isServerAdmin = userAccountService.get(userId).role == UserAccount.Role.ADMIN
    if (isUserMemberOrOwner(userId, organizationId) || isServerAdmin) {
      return
    }
    throw PermissionException()
  }

  fun checkUserIsMemberOrOwner(organizationId: Long) {
    this.checkUserIsMemberOrOwner(this.authenticationFacade.userAccount.id, organizationId)
  }

  fun isUserMemberOrOwner(userId: Long, organizationId: Long): Boolean {
    val role = organizationRoleRepository.findOneByUserIdAndOrganizationId(userId, organizationId)
    if (role != null) {
      return true
    }
    return false
  }

  fun isUserOwner(userId: Long, organizationId: Long): Boolean {
    val role = organizationRoleRepository.findOneByUserIdAndOrganizationId(userId, organizationId)
    if (role?.type == OrganizationRoleType.OWNER) {
      return true
    }
    return false
  }

  fun find(id: Long): OrganizationRole? {
    return organizationRoleRepository.findById(id).orElse(null)
  }

  fun getType(userId: Long, organizationId: Long): OrganizationRoleType {
    organizationRoleRepository.findOneByUserIdAndOrganizationId(userId, organizationId)
      ?.let { return it.type!! }
    throw PermissionException()
  }

  fun getType(organizationId: Long): OrganizationRoleType {
    return getType(authenticationFacade.userAccount.id, organizationId)
  }

  fun findType(organizationId: Long): OrganizationRoleType? {
    return findType(authenticationFacade.userAccount.id, organizationId)
  }

  fun findType(userId: Long, organizationId: Long): OrganizationRoleType? {
    organizationRoleRepository.findOneByUserIdAndOrganizationId(userId, organizationId)
      ?.let { return it.type }
    return null
  }

  fun grantRoleToUser(
    user: UserAccount,
    organization: Organization,
    organizationRoleType: OrganizationRoleType
  ) {
    OrganizationRole(user = user, organization = organization, type = organizationRoleType)
      .let {
        organization.memberRoles.add(it)
        user.organizationRoles.add(it)
        organizationRoleRepository.save(it)
      }
  }

  fun leave(organizationId: Long) {
    this.removeUser(organizationId, authenticationFacade.userAccount.id)
  }

  fun removeUser(organizationId: Long, userId: Long) {
    organizationRoleRepository.findOneByUserIdAndOrganizationId(userId, organizationId)?.let {
      organizationRoleRepository.delete(it)
    }
    userPreferencesService.refreshPreferredOrganization(userId)
  }

  fun deleteAllInOrganization(organization: Organization) {
    organizationRoleRepository.deleteByOrganization(organization)
  }

  fun delete(id: Long) {
    organizationRoleRepository.deleteById(id)
  }

  fun grantMemberRoleToUser(user: UserAccount, organization: Organization) {
    this.grantRoleToUser(user, organization, organizationRoleType = OrganizationRoleType.MEMBER)
  }

  fun grantOwnerRoleToUser(user: UserAccount, organization: Organization) {
    this.grantRoleToUser(user, organization, organizationRoleType = OrganizationRoleType.OWNER)
  }

  fun setMemberRole(organizationId: Long, userId: Long, dto: SetOrganizationRoleDto) {
    val user = userAccountService.find(userId) ?: throw NotFoundException()
    organizationRoleRepository.findOneByUserIdAndOrganizationId(user.id, organizationId)?.let {
      it.type = dto.roleType
      organizationRoleRepository.save(it)
    } ?: throw ValidationException(io.tolgee.constants.Message.USER_IS_NOT_MEMBER_OF_ORGANIZATION)
  }

  fun createForInvitation(
    invitation: Invitation,
    type: OrganizationRoleType,
    organization: Organization
  ): OrganizationRole {
    return OrganizationRole(invitation = invitation, type = type, organization = organization).let {
      organizationRoleRepository.save(it)
    }
  }

  fun acceptInvitation(organizationRole: OrganizationRole, userAccount: UserAccount) {
    organizationRole.invitation = null
    organizationRole.user = userAccount
    organizationRoleRepository.save(organizationRole)
    // switch user to the organization when accepted invitation
    organizationRole.organization?.let {
      userPreferencesService.setPreferredOrganization(it, userAccount)
    }
  }

  fun isAnotherOwnerInOrganization(id: Long): Boolean {
    return this.organizationRoleRepository
      .countAllByOrganizationIdAndTypeAndUserIdNot(
        id,
        OrganizationRoleType.OWNER,
        authenticationFacade.userAccount.id
      ) > 0
  }

  fun saveAll(organizationRoles: List<OrganizationRole>) {
    organizationRoleRepository.saveAll(organizationRoles)
  }
}
