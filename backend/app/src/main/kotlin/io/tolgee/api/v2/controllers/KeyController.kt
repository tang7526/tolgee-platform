package io.tolgee.api.v2.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import io.tolgee.activity.RequestActivity
import io.tolgee.activity.data.ActivityType
import io.tolgee.api.v2.hateoas.key.KeyImportResolvableResultModel
import io.tolgee.api.v2.hateoas.key.KeyModel
import io.tolgee.api.v2.hateoas.key.KeyModelAssembler
import io.tolgee.api.v2.hateoas.key.KeySearchResultModelAssembler
import io.tolgee.api.v2.hateoas.key.KeySearchSearchResultModel
import io.tolgee.api.v2.hateoas.key.KeyWithDataModel
import io.tolgee.api.v2.hateoas.key.KeyWithDataModelAssembler
import io.tolgee.api.v2.hateoas.key.KeyWithScreenshotsModelAssembler
import io.tolgee.api.v2.hateoas.screenshot.ScreenshotModelAssembler
import io.tolgee.component.KeyComplexEditHelper
import io.tolgee.controllers.IController
import io.tolgee.dtos.request.GetKeysRequestDto
import io.tolgee.dtos.request.key.ComplexEditKeyDto
import io.tolgee.dtos.request.key.CreateKeyDto
import io.tolgee.dtos.request.key.DeleteKeysDto
import io.tolgee.dtos.request.key.EditKeyDto
import io.tolgee.dtos.request.translation.ImportKeysDto
import io.tolgee.dtos.request.translation.importKeysResolvable.ImportKeysResolvableDto
import io.tolgee.exceptions.NotFoundException
import io.tolgee.model.Project
import io.tolgee.model.enums.Scope
import io.tolgee.model.key.Key
import io.tolgee.security.apiKeyAuth.AccessWithApiKey
import io.tolgee.security.project_auth.AccessWithProjectPermission
import io.tolgee.security.project_auth.ProjectHolder
import io.tolgee.service.key.KeySearchResultView
import io.tolgee.service.key.KeyService
import io.tolgee.service.security.SecurityService
import org.springdoc.api.annotations.ParameterObject
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.data.web.SortDefault
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Suppress("MVCPathVariableInspection")
@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(
  value = [
    "/v2/projects/{projectId}/keys",
    "/v2/projects/keys"
  ]
)
@Tag(name = "Localization keys", description = "Manipulates localization keys and their translations and metadata")
class KeyController(
  private val keyService: KeyService,
  private val projectHolder: ProjectHolder,
  private val keyModelAssembler: KeyModelAssembler,
  private val keyWithDataModelAssembler: KeyWithDataModelAssembler,
  private val securityService: SecurityService,
  private val applicationContext: ApplicationContext,
  private val keyPagedResourcesAssembler: PagedResourcesAssembler<Key>,
  private val keySearchResultModelAssembler: KeySearchResultModelAssembler,
  private val pagedResourcesAssembler: PagedResourcesAssembler<KeySearchResultView>,
  private val screenshotModelAssembler: ScreenshotModelAssembler,
  private val keyWithScreenshotsModelAssembler: KeyWithScreenshotsModelAssembler
) : IController {
  @PostMapping(value = ["/create", ""])
  @AccessWithProjectPermission(Scope.KEYS_CREATE)
  @AccessWithApiKey()
  @Operation(summary = "Creates new key")
  @ResponseStatus(HttpStatus.CREATED)
  @RequestActivity(ActivityType.CREATE_KEY)
  fun create(@RequestBody @Valid dto: CreateKeyDto): ResponseEntity<KeyWithDataModel> {
    if (dto.screenshotUploadedImageIds != null || !dto.screenshots.isNullOrEmpty()) {
      projectHolder.projectEntity.checkScreenshotsUploadPermission()
    }

    dto.translations?.keys?.let { languageTags ->
      securityService.checkLanguageTranslatePermissionByTag(projectHolder.project.id, languageTags)
    }

    val key = keyService.create(projectHolder.projectEntity, dto)
    return ResponseEntity(keyWithDataModelAssembler.toModel(key), HttpStatus.CREATED)
  }

  @PutMapping(value = ["/{id}/complex-update"])
  @Operation(summary = "More")
  // key permissions are checked separately in method body
  @AccessWithApiKey()
  @Transactional
  fun complexEdit(@PathVariable id: Long, @RequestBody @Valid dto: ComplexEditKeyDto): KeyWithDataModel {
    return KeyComplexEditHelper(applicationContext, id, dto).doComplexUpdate()
  }

  @PutMapping(value = ["/{id}"])
  @Operation(summary = "Edits key name")
  @AccessWithProjectPermission(Scope.KEYS_EDIT)
  @AccessWithApiKey()
  @RequestActivity(ActivityType.KEY_NAME_EDIT)
  fun edit(@PathVariable id: Long, @RequestBody @Valid dto: EditKeyDto): KeyModel {
    val key = keyService.findOptional(id).orElseThrow { NotFoundException() }
    key.checkInProject()
    return keyService.edit(id, dto).model
  }

  @DeleteMapping(value = ["/{ids:[0-9,]+}"])
  @Transactional
  @Operation(summary = "Deletes one or multiple keys by their IDs")
  @AccessWithProjectPermission(Scope.KEYS_DELETE)
  @AccessWithApiKey()
  @RequestActivity(ActivityType.KEY_DELETE)
  fun delete(@PathVariable ids: Set<Long>) {
    keyService.findAllWithProjectsAndMetas(ids).forEach { it.checkInProject() }
    keyService.deleteMultiple(ids)
  }

  @GetMapping(value = [""])
  @Transactional
  @Operation(summary = "Returns all keys in the project")
  @AccessWithProjectPermission(Scope.KEYS_VIEW)
  @AccessWithApiKey()
  fun getAll(
    @ParameterObject
    @SortDefault("id")
    pageable: Pageable
  ): PagedModel<KeyModel> {
    val data = keyService.getPaged(projectHolder.project.id, pageable)
    return keyPagedResourcesAssembler.toModel(data, keyModelAssembler)
  }

  @DeleteMapping(value = [""])
  @Transactional
  @Operation(summary = "Deletes one or multiple keys by their IDs in request body")
  @AccessWithProjectPermission(Scope.KEYS_DELETE)
  @AccessWithApiKey()
  @RequestActivity(ActivityType.KEY_DELETE)
  fun delete(@RequestBody @Valid dto: DeleteKeysDto) {
    delete(dto.ids.toSet())
  }

  @PostMapping("/import")
  @AccessWithApiKey()
  @AccessWithProjectPermission(Scope.KEYS_EDIT)
  @Operation(
    summary = "Imports new keys with translations. If key already exists, its translations and tags" +
      " are not updated."
  )
  @RequestActivity(ActivityType.IMPORT)
  fun importKeys(@RequestBody @Valid dto: ImportKeysDto) {
    securityService.checkLanguageTranslatePermissionByTag(
      projectHolder.project.id,
      dto.keys.flatMap { it.translations.keys }.toSet()
    )

    keyService.importKeys(dto.keys, projectHolder.projectEntity)
  }

  @PostMapping("/import-resolvable")
  @AccessWithApiKey
  @AccessWithProjectPermission(Scope.KEYS_EDIT)
  @Operation(summary = "Import's new keys with translations. Translations can be updated, when specified.")
  @RequestActivity(ActivityType.IMPORT)
  fun importKeys(@RequestBody @Valid dto: ImportKeysResolvableDto): KeyImportResolvableResultModel {
    val uploadedImageToScreenshotMap =
      keyService.importKeysResolvable(dto.keys, projectHolder.projectEntity)
    val screenshots = uploadedImageToScreenshotMap.screenshots
      .map { (uploadedImageId, screenshot) ->
        uploadedImageId to screenshotModelAssembler.toModel(screenshot)
      }.toMap()

    val keys = uploadedImageToScreenshotMap.keys
      .map { key -> keyModelAssembler.toModel(key) }

    return KeyImportResolvableResultModel(keys, screenshots)
  }

  @GetMapping("/search")
  @AccessWithApiKey()
  @AccessWithProjectPermission(Scope.KEYS_VIEW)
  @Operation(
    summary = "This endpoint helps you to find desired key by keyName, " +
      "base translation or translation in specified language."
  )
  fun searchForKey(
    @RequestParam
    @Parameter(description = "Search query")
    search: String,
    @RequestParam
    @Parameter(description = "Language to search in")
    languageTag: String? = null,
    @ParameterObject pageable: Pageable,
  ): PagedModel<KeySearchSearchResultModel> {
    val result = keyService.searchKeys(search, languageTag, projectHolder.project, pageable)
    return pagedResourcesAssembler.toModel(result, keySearchResultModelAssembler)
  }

  @PostMapping("/info")
  @AccessWithApiKey()
  @AccessWithProjectPermission(Scope.KEYS_VIEW)
  @Operation(
    summary = "Returns information about keys. (KeyData, Screenshots, Translation in specified language)" +
      "If key is not found, it's not included in the response."
  )
  fun getInfo(
    @RequestBody
    @Valid
    dto: GetKeysRequestDto,
  ): CollectionModel<KeyWithDataModel> {
    val result = keyService.getKeysInfo(dto, projectHolder.project.id)
    return keyWithScreenshotsModelAssembler.toCollectionModel(result)
  }

  private fun Key.checkInProject() {
    keyService.checkInProject(this, projectHolder.project.id)
  }

  private val Key.model: KeyModel
    get() = keyModelAssembler.toModel(this)

  private fun Project.checkScreenshotsUploadPermission() {
    securityService.checkScreenshotsUploadPermission(this.id)
  }
}
