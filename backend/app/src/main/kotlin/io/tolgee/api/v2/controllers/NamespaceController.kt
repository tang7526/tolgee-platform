package io.tolgee.api.v2.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.tolgee.activity.RequestActivity
import io.tolgee.activity.data.ActivityType
import io.tolgee.api.v2.hateoas.key.namespace.NamespaceModel
import io.tolgee.api.v2.hateoas.key.namespace.NamespaceModelAssembler
import io.tolgee.api.v2.hateoas.key.namespace.UsedNamespaceModel
import io.tolgee.api.v2.hateoas.key.namespace.UsedNamespaceModelAssembler
import io.tolgee.controllers.IController
import io.tolgee.dtos.request.key.UpdateNamespaceDto
import io.tolgee.model.Permission
import io.tolgee.model.enums.ApiScope
import io.tolgee.model.key.Namespace
import io.tolgee.security.apiKeyAuth.AccessWithApiKey
import io.tolgee.security.project_auth.AccessWithAnyProjectPermission
import io.tolgee.security.project_auth.AccessWithProjectPermission
import io.tolgee.security.project_auth.ProjectHolder
import io.tolgee.service.key.NamespaceService
import org.springdoc.api.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.data.web.SortDefault
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Suppress("MVCPathVariableInspection")
@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(
  value = [
    "/v2/projects/{projectId}/",
    "/v2/projects/"
  ]
)
@Tag(name = "Namespaces", description = "Manipulates key namespaces")
class NamespaceController(
  private val namespaceService: NamespaceService,
  private val projectHolder: ProjectHolder,
  private val namespaceModelAssembler: NamespaceModelAssembler,
  private val pagedResourcesAssembler: PagedResourcesAssembler<Namespace>,
  private val usedNamespaceModelAssembler: UsedNamespaceModelAssembler
) : IController {

  @GetMapping(value = ["namespaces"])
  @Operation(summary = "Returns all project namespaces")
  @AccessWithAnyProjectPermission
  @AccessWithApiKey
  fun getAllNamespaces(
    @ParameterObject @SortDefault("id") pageable: Pageable
  ): PagedModel<NamespaceModel> {
    val namespaces = namespaceService.getAllInProject(projectHolder.project.id, pageable)
    return pagedResourcesAssembler.toModel(namespaces, namespaceModelAssembler)
  }

  @GetMapping(value = ["used-namespaces"])
  @Operation(summary = "Returns all used project namespaces. Response contains default (null) namespace if used.")
  @AccessWithAnyProjectPermission
  @AccessWithApiKey
  fun getUsedNamespaces(): CollectionModel<UsedNamespaceModel> {
    val namespaces = namespaceService.getAllInProject(projectHolder.project.id)
      .map { it.id as Long? to it.name as String? }.toMutableList()
    val isDefaultUsed = namespaceService.isDefaultUsed(projectHolder.project.id)
    if (isDefaultUsed) {
      namespaces.add(0, null to null)
    }
    return usedNamespaceModelAssembler.toCollectionModel(namespaces)
  }

  @PutMapping(value = ["/namespaces/{id}"])
  @Operation(summary = "Update namespace")
  @AccessWithProjectPermission(Permission.ProjectPermissionType.EDIT)
  @AccessWithApiKey(scopes = [ApiScope.KEYS_EDIT])
  @RequestActivity(ActivityType.NAMESPACE_EDIT)
  fun update(
    @PathVariable id: Long,
    @RequestBody @Valid dto: UpdateNamespaceDto
  ): NamespaceModel {
    val namespace = namespaceService.get(projectHolder.project.id, id)
    namespaceService.update(namespace, dto)
    return namespaceModelAssembler.toModel(namespace)
  }

  @GetMapping(value = ["/namespace-by-name/{name}"])
  @Operation(summary = "Update namespace")
  @AccessWithAnyProjectPermission
  @AccessWithApiKey
  fun getByName(
    @PathVariable name: String,
  ): NamespaceModel {
    val namespace = namespaceService.get(projectId = projectHolder.project.id, name)
    return namespaceModelAssembler.toModel(namespace)
  }
}
