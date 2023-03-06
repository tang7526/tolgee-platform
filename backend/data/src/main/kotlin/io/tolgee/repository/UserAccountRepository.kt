package io.tolgee.repository

import io.tolgee.model.UserAccount
import io.tolgee.model.views.UserAccountInProjectView
import io.tolgee.model.views.UserAccountWithOrganizationRoleView
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserAccountRepository : JpaRepository<UserAccount, Long> {
  fun findByUsername(username: String?): Optional<UserAccount>

  @Query("from UserAccount ua where ua.username = :username and ua.deletedAt is null and ua.disabledAt is null")
  fun findActive(username: String): UserAccount?

  @Query("from UserAccount ua where ua.id = :id and ua.deletedAt is null and ua.disabledAt is null")
  fun findActive(id: Long): UserAccount?

  @Modifying
  @Query(
    """update UserAccount ua 
    set 
     ua.deletedAt = now(), 
     ua.tokensValidNotBefore = now(),
     ua.password = null, 
     ua.totpKey = null, 
     ua.mfaRecoveryCodes = null,
     ua.thirdPartyAuthId = null,
     ua.thirdPartyAuthType = null,
     ua.avatarHash = null,
     ua.username = 'former',
     ua.name = 'Former user'
     where ua = :user
     """
  )
  fun softDeleteUser(user: UserAccount)

  @Query(
    """
    from UserAccount ua 
      where ua.thirdPartyAuthId = :thirdPartyAuthId 
        and ua.thirdPartyAuthType = :thirdPartyAuthType
        and ua.deletedAt is null
        and ua.disabledAt is null
  """
  )
  fun findThirdByThirdParty(
    thirdPartyAuthId: String,
    thirdPartyAuthType: String
  ): Optional<UserAccount>

  @Query(
    """select userAccount.id as id, userAccount.name as name, userAccount.username as username, memberRole.type as organizationRole from UserAccount userAccount 
        join OrganizationRole memberRole on memberRole.user = userAccount and memberRole.organization.id = :organizationId
        where ((lower(userAccount.name)
        like lower(concat('%', cast(:search as text),'%')) 
        or lower(userAccount.username) like lower(concat('%', cast(:search as text),'%'))) or cast(:search as text) is null)
        and userAccount.deletedAt is null
        """
  )
  fun getAllInOrganization(
    organizationId: Long,
    pageable: Pageable,
    search: String
  ): Page<UserAccountWithOrganizationRoleView>

  @Query(
    """
        select ua.id as id, ua.name as name, ua.username as username, p as directPermission, orl.type as organizationRole 
        from UserAccount ua, Project r 
        left join fetch Permission p on ua = p.user and p.project.id = :projectId
        left join OrganizationRole orl on orl.user = ua and r.organizationOwner = orl.organization
        where r.id = :projectId and (p is not null or orl is not null)
        and (:exceptUserId is null or ua.id <> :exceptUserId)
        and ((lower(ua.name)
        like lower(concat('%', cast(:search as text),'%'))
        or lower(ua.username) like lower(concat('%', cast(:search as text),'%'))) or cast(:search as text) is null)
        and ua.deletedAt is null
    """
  )
  fun getAllInProject(
    projectId: Long,
    pageable: Pageable,
    search: String? = "",
    exceptUserId: Long? = null
  ): Page<UserAccountInProjectView>

  @Query(
    """
    select ua
    from UserAccount ua
    left join ua.organizationRoles orl
    where orl is null
      and ua.deletedAt is null
  """
  )
  fun findAllWithoutAnyOrganization(pageable: Pageable): Page<UserAccount>

  @Query(
    """
    select ua.id
    from UserAccount ua
    left join ua.organizationRoles orl
    where orl is null
      and ua.deletedAt is null
  """
  )
  fun findAllWithoutAnyOrganizationIds(): List<Long>

  @Query(
    """
    from UserAccount userAccount
    where ((lower(userAccount.name)
      like lower(concat('%', cast(:search as text),'%')) 
      or lower(userAccount.username) like lower(concat('%', cast(:search as text),'%'))) or cast(:search as text) is null)
      and userAccount.deletedAt is null
  """
  )
  fun findAllWithDisabledPaged(search: String?, pageable: Pageable): Page<UserAccount>

  @Query(
    value = """
    select ua from UserAccount ua where id in (:ids)
  """
  )
  fun getAllByIdsIncludingDeleted(ids: Set<Long>): MutableList<UserAccount>

  @Query(
    value = """
    select count(ua) from UserAccount ua where ua.disabledAt is null
  """
  )
  fun countAllEnabled(): Long

  @Query(
    value = """
    select ua from UserAccount ua where ua.id = :id and ua.disabledAt is not null
  """
  )
  fun findDisabled(id: Long): UserAccount
}
