/*
 * Copyright (c) 2020. Tolgee
 */

package io.tolgee.api.v2.controllers.translation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.tags.Tags
import io.tolgee.activity.ActivityHolder
import io.tolgee.activity.ActivityService
import io.tolgee.activity.RequestActivity
import io.tolgee.activity.data.ActivityType
import io.tolgee.api.v2.hateoas.translations.KeysWithTranslationsPageModel
import io.tolgee.api.v2.hateoas.translations.KeysWithTranslationsPagedResourcesAssembler
import io.tolgee.api.v2.hateoas.translations.SetTranslationsResponseModel
import io.tolgee.api.v2.hateoas.translations.TranslationHistoryModel
import io.tolgee.api.v2.hateoas.translations.TranslationHistoryModelAssembler
import io.tolgee.api.v2.hateoas.translations.TranslationModel
import io.tolgee.api.v2.hateoas.translations.TranslationModelAssembler
import io.tolgee.component.ProjectTranslationLastModifiedManager
import io.tolgee.controllers.IController
import io.tolgee.dtos.query_results.TranslationHistoryView
import io.tolgee.dtos.request.translation.GetTranslationsParams
import io.tolgee.dtos.request.translation.SelectAllResponse
import io.tolgee.dtos.request.translation.SetTranslationsWithKeyDto
import io.tolgee.dtos.request.translation.TranslationFilters
import io.tolgee.exceptions.BadRequestException
import io.tolgee.model.Language
import io.tolgee.model.Permission
import io.tolgee.model.Screenshot
import io.tolgee.model.enums.ApiScope
import io.tolgee.model.enums.TranslationState
import io.tolgee.model.key.Key
import io.tolgee.model.translation.Translation
import io.tolgee.model.views.KeyWithTranslationsView
import io.tolgee.security.AuthenticationFacade
import io.tolgee.security.apiKeyAuth.AccessWithApiKey
import io.tolgee.security.project_auth.AccessWithAnyProjectPermission
import io.tolgee.security.project_auth.AccessWithProjectPermission
import io.tolgee.security.project_auth.ProjectHolder
import io.tolgee.service.LanguageService
import io.tolgee.service.key.KeyService
import io.tolgee.service.key.ScreenshotService
import io.tolgee.service.query_builders.CursorUtil
import io.tolgee.service.security.SecurityService
import io.tolgee.service.translation.TranslationService
import org.springdoc.api.annotations.ParameterObject
import org.springframework.beans.propertyeditors.CustomCollectionEditor
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.data.web.SortDefault
import org.springframework.hateoas.PagedModel
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import java.util.concurrent.TimeUnit
import javax.validation.Valid

@Suppress("MVCPathVariableInspection", "SpringJavaInjectionPointsAutowiringInspection")
@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(
  value = [
    "/v2/projects/{projectId:[0-9]+}/translations",
    "/v2/projects/translations"
  ]
)
@Tags(
  value = [
    Tag(name = "Translations", description = "Operations related to translations in project"),
  ]
)
class TranslationsController(
  private val projectHolder: ProjectHolder,
  private val translationService: TranslationService,
  private val keyService: KeyService,
  private val pagedAssembler: KeysWithTranslationsPagedResourcesAssembler,
  private val historyPagedAssembler: PagedResourcesAssembler<TranslationHistoryView>,
  private val historyModelAssembler: TranslationHistoryModelAssembler,
  private val translationModelAssembler: TranslationModelAssembler,
  private val languageService: LanguageService,
  private val securityService: SecurityService,
  private val authenticationFacade: AuthenticationFacade,
  private val screenshotService: ScreenshotService,
  private val activityHolder: ActivityHolder,
  private val activityService: ActivityService,
  private val projectTranslationLastModifiedManager: ProjectTranslationLastModifiedManager
) : IController {
  @GetMapping(value = ["/{languages}"])
  @AccessWithAnyProjectPermission
  @AccessWithApiKey(scopes = [ApiScope.TRANSLATIONS_VIEW])
  @Operation(
    summary = "Returns all translations for specified languages",
    responses = [
      ApiResponse(
        responseCode = "200",
        content = [
          Content(
            schema = Schema(
              example = """{"en": {"what a key": "Translated value", "another key": "Another key translated"},""" +
                """"cs": {"what a key": "Překlad", "another key": "Další překlad"}}"""
            )
          )
        ]
      )
    ]
  )
  fun getAllTranslations(
    @Parameter(description = "Comma-separated language tags to return translations in.", example = "en,de,fr")
    @PathVariable("languages") languages: Set<String>,
    @Parameter(description = "Namespace to return")
    ns: String? = "",
    @Parameter(
      description = """Delimiter to structure response content. 

e.g. For key "home.header.title" would result in {"home": {"header": {"title": "Hello"}}} structure.

When null, resulting file will be a flat key-value object.
    """,
    )
    @RequestParam(value = "structureDelimiter", defaultValue = ".", required = false)
    structureDelimiter: Char?,
    request: WebRequest
  ): ResponseEntity<Map<String, Any>>? {
    val lastModified: Long = projectTranslationLastModifiedManager.getLastModified(projectHolder.project.id)

    if (request.checkNotModified(lastModified)) {
      return null
    }

    val response = translationService.getTranslations(
      languageTags = languages,
      namespace = ns,
      projectId = projectHolder.project.id,
      structureDelimiter = request.getStructureDelimiter()
    )

    return ResponseEntity.ok()
      .lastModified(lastModified)
      .cacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS))
      .body(
        response
      )
  }

  /**
   * It has to be handled manually since spring returns default value even when empty value provided
   */
  private fun WebRequest.getStructureDelimiter(): Char? {
    val structureDelimiterParam = this.parameterMap["structureDelimiter"]?.first() ?: return '.'
    if (structureDelimiterParam == "") {
      return null
    }
    return structureDelimiterParam.toCharArray().first()
  }

  @PutMapping("")
  @AccessWithApiKey(scopes = [ApiScope.TRANSLATIONS_EDIT])
  @AccessWithProjectPermission(permission = Permission.ProjectPermissionType.TRANSLATE)
  @Operation(summary = "Sets translations for existing key")
  @RequestActivity(ActivityType.SET_TRANSLATIONS)
  fun setTranslations(@RequestBody @Valid dto: SetTranslationsWithKeyDto): SetTranslationsResponseModel {
    val key = keyService.get(projectHolder.project.id, dto.key, dto.namespace)
    securityService.checkLanguageTagPermissions(dto.translations.keys, projectHolder.project.id)

    val modifiedTranslations = translationService.setForKey(key, dto.translations)

    val translations = dto.languagesToReturn
      ?.let { languagesToReturn ->
        key.translations
          .filter { languagesToReturn.contains(it.language.tag) }
          .associateBy { it.language.tag }
      }
      ?: modifiedTranslations

    return getSetTranslationsResponse(key, translations)
  }

  @PostMapping("")
  @AccessWithApiKey([ApiScope.TRANSLATIONS_EDIT])
  @AccessWithProjectPermission(permission = Permission.ProjectPermissionType.EDIT)
  @Operation(summary = "Sets translations for existing or not existing key")
  @RequestActivity(ActivityType.SET_TRANSLATIONS)
  fun createOrUpdateTranslations(@RequestBody @Valid dto: SetTranslationsWithKeyDto): SetTranslationsResponseModel {
    val key = keyService.find(projectHolder.projectEntity.id, dto.key, dto.namespace)?.also {
      activityHolder.activity = ActivityType.SET_TRANSLATIONS
    } ?: let {
      checkKeyEditScope()
      activityHolder.activity = ActivityType.CREATE_KEY
      keyService.create(projectHolder.projectEntity, dto.key, dto.namespace)
    }
    val translations = translationService.setForKey(key, dto.translations)
    return getSetTranslationsResponse(key, translations)
  }

  @PutMapping("/{translationId}/set-state/{state}")
  @AccessWithApiKey([ApiScope.TRANSLATIONS_EDIT])
  @AccessWithProjectPermission(permission = Permission.ProjectPermissionType.TRANSLATE)
  @Operation(summary = "Sets translation state")
  @RequestActivity(ActivityType.SET_TRANSLATION_STATE)
  fun setTranslationState(@PathVariable translationId: Long, @PathVariable state: TranslationState): TranslationModel {
    val translation = translationService.get(translationId)
    translation.checkFromProject()
    securityService.checkLanguageTranslatePermission(translation)
    return translationModelAssembler.toModel(translationService.setState(translation, state))
  }

  @InitBinder("translationFilters")
  fun customizeBinding(binder: WebDataBinder) {
    binder.registerCustomEditor(
      List::class.java,
      TranslationFilters::filterKeyName.name,
      CustomCollectionEditor(List::class.java)
    )
  }

  @GetMapping(value = [""])
  @AccessWithApiKey([ApiScope.TRANSLATIONS_VIEW])
  @AccessWithProjectPermission(Permission.ProjectPermissionType.VIEW)
  @Operation(summary = "Returns translations in project")
  fun getTranslations(
    @ParameterObject @ModelAttribute("translationFilters") params: GetTranslationsParams,
    @ParameterObject pageable: Pageable
  ): KeysWithTranslationsPageModel {

    val languages: Set<Language> = languageService
      .getLanguagesForTranslationsView(params.languages, projectHolder.project.id)

    val pageableWithSort = getSafeSortPageable(pageable)
    val data = translationService.getViewData(projectHolder.project.id, pageableWithSort, params, languages)

    val keysWithScreenshots = getScreenshots(data.map { it.keyId }.toList())

    if (keysWithScreenshots != null) {
      data.content.forEach { it.screenshots = keysWithScreenshots[it.keyId] ?: listOf() }
    }

    val cursor = if (data.content.isNotEmpty()) CursorUtil.getCursor(data.content.last(), data.sort) else null
    return pagedAssembler.toTranslationModel(data, languages, cursor)
  }

  @GetMapping(value = ["select-all"])
  @AccessWithApiKey([ApiScope.TRANSLATIONS_VIEW])
  @AccessWithProjectPermission(Permission.ProjectPermissionType.VIEW)
  @Operation(summary = "Get select all keys")
  fun getSelectAllKeyIds(
    @ParameterObject @ModelAttribute("translationFilters") params: TranslationFilters,
  ): SelectAllResponse {
    val languages: Set<Language> = languageService
      .getLanguagesForTranslationsView(params.languages, projectHolder.project.id)

    return SelectAllResponse(
      translationService.getSelectAllKeys(
        projectId = projectHolder.project.id,
        params = params,
        languages = languages
      )
    )
  }

  @PutMapping(value = ["/{translationId:[0-9]+}/dismiss-auto-translated-state"])
  @AccessWithApiKey([ApiScope.TRANSLATIONS_EDIT])
  @AccessWithProjectPermission(Permission.ProjectPermissionType.TRANSLATE)
  @Operation(summary = """Removes "auto translated" indication""")
  @RequestActivity(ActivityType.DISMISS_AUTO_TRANSLATED_STATE)
  fun dismissAutoTranslatedState(
    @PathVariable translationId: Long
  ): TranslationModel {
    val translation = translationService.get(translationId)
    translation.checkFromProject()
    translationService.dismissAutoTranslated(translation)
    return translationModelAssembler.toModel(translation)
  }

  @PutMapping(value = ["/{translationId:[0-9]+}/set-outdated-flag/{state}"])
  @AccessWithApiKey([ApiScope.TRANSLATIONS_EDIT])
  @AccessWithProjectPermission(Permission.ProjectPermissionType.TRANSLATE)
  @Operation(summary = """Set's "outdated" indication""")
  @RequestActivity(ActivityType.SET_OUTDATED_FLAG)
  fun setOutdated(
    @PathVariable translationId: Long,
    @PathVariable state: Boolean
  ): TranslationModel {
    val translation = translationService.get(translationId)
    translation.checkFromProject()
    translationService.setOutdated(translation, state)
    return translationModelAssembler.toModel(translation)
  }

  @GetMapping(value = ["/{translationId:[0-9]+}/history"])
  @AccessWithApiKey([ApiScope.TRANSLATIONS_VIEW])
  @AccessWithProjectPermission(Permission.ProjectPermissionType.VIEW)
  @Operation(
    summary = """Returns history of specific translation. 

Sorting is not supported for supported. It is automatically sorted from newest to oldest."""
  )
  fun getTranslationHistory(
    @PathVariable translationId: Long,
    @ParameterObject @SortDefault(sort = ["timestamp"], direction = Sort.Direction.DESC) pageable: Pageable
  ): PagedModel<TranslationHistoryModel> {
    val translation = translationService.get(translationId)
    translation.checkFromProject()
    val translations = activityService.getTranslationHistory(translation.id, pageable)
    return historyPagedAssembler.toModel(translations, historyModelAssembler)
  }

  private fun getScreenshots(keyIds: Collection<Long>): Map<Long, List<Screenshot>>? {
    if (
      !authenticationFacade.isApiKeyAuthentication ||
      authenticationFacade.apiKey.scopesEnum.contains(ApiScope.SCREENSHOTS_VIEW)
    ) {
      return screenshotService.getScreenshotsForKeys(keyIds)
    }
    return null
  }

  private fun getSetTranslationsResponse(key: Key, translations: Map<String, Translation>):
    SetTranslationsResponseModel {
    return SetTranslationsResponseModel(
      keyId = key.id,
      keyName = key.name,
      keyNamespace = key.namespace?.name,
      translations = translations.entries.associate { (languageTag, translation) ->
        languageTag to translationModelAssembler.toModel(translation)
      }
    )
  }

  private fun checkKeyEditScope() {
    if (authenticationFacade.isApiKeyAuthentication) {
      securityService.checkApiKeyScopes(setOf(ApiScope.KEYS_EDIT), authenticationFacade.apiKey)
    }
  }

  private fun getSafeSortPageable(pageable: Pageable): Pageable {
    var sort = pageable.sort
    if (sort.getOrderFor(KeyWithTranslationsView::keyId.name) == null) {
      sort = sort.and(Sort.by(Sort.Direction.ASC, KeyWithTranslationsView::keyId.name))
    }

    return PageRequest.of(pageable.pageNumber, pageable.pageSize, sort)
  }

  private fun Translation.checkFromProject() {
    if (this.key.project.id != projectHolder.project.id) {
      throw BadRequestException(io.tolgee.constants.Message.TRANSLATION_NOT_FROM_PROJECT)
    }
  }
}
