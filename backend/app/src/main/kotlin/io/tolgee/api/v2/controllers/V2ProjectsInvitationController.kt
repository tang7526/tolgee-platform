/*
 * Copyright (c) 2020. Tolgee
 */

package io.tolgee.api.v2.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.tolgee.api.v2.hateoas.invitation.ProjectInvitationModel
import io.tolgee.api.v2.hateoas.invitation.ProjectInvitationModelAssembler
import io.tolgee.constants.Message
import io.tolgee.dtos.misc.CreateProjectInvitationParams
import io.tolgee.dtos.request.project.ProjectInviteUserDto
import io.tolgee.ee.service.EeInvitationService
import io.tolgee.exceptions.BadRequestException
import io.tolgee.facade.ProjectPermissionFacade
import io.tolgee.model.enums.Scope
import io.tolgee.security.NeedsSuperJwtToken
import io.tolgee.security.project_auth.AccessWithProjectPermission
import io.tolgee.security.project_auth.ProjectHolder
import io.tolgee.service.InvitationService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Suppress(names = ["MVCPathVariableInspection", "SpringJavaInjectionPointsAutowiringInspection"])
@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(value = ["/v2/projects"])
@Tag(name = "Projects")
class
V2ProjectsInvitationController(
  private val projectHolder: ProjectHolder,
  private val invitationService: InvitationService,
  private val projectInvitationModelAssembler: ProjectInvitationModelAssembler,
  private val projectPermissionFacade: ProjectPermissionFacade,
  private val eeInvitationService: EeInvitationService
) {
  @PutMapping("/{projectId}/invite")
  @Operation(summary = "Generates user invitation link for project")
  @AccessWithProjectPermission(Scope.MEMBERS_EDIT)
  @NeedsSuperJwtToken
  fun inviteUser(@RequestBody @Valid invitation: ProjectInviteUserDto): ProjectInvitationModel {
    validatePermissions(invitation)

    val languagesPermissions = projectPermissionFacade.getLanguages(invitation, projectHolder.project.id)

    val params = CreateProjectInvitationParams(
      project = projectHolder.projectEntity,
      type = invitation.type,
      scopes = invitation.scopes,
      email = invitation.email,
      name = invitation.name,
      languagePermissions = languagesPermissions
    )

    val created = if (!params.scopes.isNullOrEmpty()) {
      eeInvitationService.create(params)
    } else {
      invitationService.create(params)
    }

    return projectInvitationModelAssembler.toModel(created)
  }

  fun validatePermissions(invitation: ProjectInviteUserDto) {
    if (!(invitation.scopes.isNullOrEmpty() xor (invitation.type == null))) {
      throw BadRequestException(Message.SET_EXACTLY_ONE_OF_SCOPES_OR_TYPE)
    }
  }
}
