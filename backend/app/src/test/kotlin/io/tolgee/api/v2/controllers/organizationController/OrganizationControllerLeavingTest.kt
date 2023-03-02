package io.tolgee.api.v2.controllers.organizationController

import io.tolgee.development.testDataBuilder.data.OrganizationTestData
import io.tolgee.fixtures.andAssertError
import io.tolgee.fixtures.andIsBadRequest
import io.tolgee.fixtures.andIsOk
import io.tolgee.testing.assertions.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest

@SpringBootTest
@AutoConfigureMockMvc
class OrganizationControllerLeavingTest : BaseOrganizationControllerTest() {

  @Test
  fun testLeaveOrganization() {
    val testOrg = executeInNewTransaction { this.organizationService.create(dummyDto, userAccount!!) }
    organizationRoleService.grantOwnerRoleToUser(dbPopulator.createUserIfNotExists("secondOwner"), testOrg)
    assertThat(getPermittedOrgs().find { testOrg.id == it.id }).isNotNull
    performAuthPut("/v2/organizations/${testOrg.id}/leave", null).andIsOk
    assertThat(getPermittedOrgs().find { testOrg.id == it.id }).isNull()
  }

  private fun getPermittedOrgs() =
    organizationRepository.findAllPermitted(userAccount!!.id, PageRequest.of(0, 20)).content

  @Test
  fun `leave will reset preferred`() {
    val testData = OrganizationTestData()
    testDataService.saveTestData(testData.root)
    userAccount = testData.jirina

    assertThat(userPreferencesService.find(testData.jirina.id)!!.preferredOrganization?.name)
      .isEqualTo(testData.jirinaOrg.name)

    performAuthPut("/v2/organizations/${testData.jirinaOrg.id}/leave", null).andIsOk

    assertThat(userPreferencesService.find(testData.jirina.id)!!.preferredOrganization?.name)
      .isNotEqualTo(testData.jirinaOrg.name)
  }

  @Test
  fun testLeaveOrganizationNoOtherOwner() {
    val organization = executeInNewTransaction { this.organizationService.create(dummyDto, userAccount!!) }
    organizationRepository.findAllPermitted(userAccount!!.id, PageRequest.of(0, 20)).content.let {
      assertThat(it).isNotEmpty
    }
    performAuthPut("/v2/organizations/${organization.id}/leave", null)
      .andIsBadRequest
      .andAssertError
      .isCustomValidation.hasMessage("organization_has_no_other_owner")
  }
}
