package io.tolgee.component

import io.tolgee.activity.ActivityHolder
import io.tolgee.activity.data.ActivityType
import io.tolgee.api.v2.hateoas.key.KeyWithDataModel
import io.tolgee.api.v2.hateoas.key.KeyWithDataModelAssembler
import io.tolgee.dtos.request.key.ComplexEditKeyDto
import io.tolgee.model.Permission
import io.tolgee.model.Project
import io.tolgee.model.enums.ApiScope
import io.tolgee.model.key.Key
import io.tolgee.security.AuthenticationFacade
import io.tolgee.security.project_auth.ProjectHolder
import io.tolgee.service.LanguageService
import io.tolgee.service.key.KeyService
import io.tolgee.service.key.ScreenshotService
import io.tolgee.service.key.TagService
import io.tolgee.service.security.SecurityService
import io.tolgee.service.translation.TranslationService
import org.springframework.context.ApplicationContext
import kotlin.properties.Delegates

class KeyComplexEditHelper(
  applicationContext: ApplicationContext,
  private val keyId: Long,
  private val dto: ComplexEditKeyDto
) {
  private val keyWithDataModelAssembler: KeyWithDataModelAssembler =
    applicationContext.getBean(KeyWithDataModelAssembler::class.java)
  private val keyService: KeyService = applicationContext.getBean(KeyService::class.java)
  private val securityService: SecurityService = applicationContext.getBean(SecurityService::class.java)
  private val languageService: LanguageService = applicationContext.getBean(LanguageService::class.java)
  private val projectHolder: ProjectHolder = applicationContext.getBean(ProjectHolder::class.java)
  private val translationService: TranslationService = applicationContext.getBean(TranslationService::class.java)
  private val tagService: TagService = applicationContext.getBean(TagService::class.java)
  private val screenshotService: ScreenshotService = applicationContext.getBean(ScreenshotService::class.java)
  private val authenticationFacade: AuthenticationFacade = applicationContext.getBean(AuthenticationFacade::class.java)
  private val activityHolder: ActivityHolder = applicationContext.getBean(ActivityHolder::class.java)

  private lateinit var key: Key
  private var modifiedTranslations: Map<String, String?>? = null
  private val dtoTags = dto.tags

  private var areTranslationsModified by Delegates.notNull<Boolean>()
  private var areTagsModified by Delegates.notNull<Boolean>()
  private var isKeyModified by Delegates.notNull<Boolean>()
  private var isScreenshotDeleted by Delegates.notNull<Boolean>()
  private var isScreenshotAdded by Delegates.notNull<Boolean>()

  fun doComplexUpdate(): KeyWithDataModel {
    prepareData()
    prepareConditions()
    setActivityHolder()

    if (modifiedTranslations != null && areTranslationsModified) {
      securityService.checkLanguageTagPermissions(modifiedTranslations!!.keys, projectHolder.project.id)
      translationService.setForKey(key, translations = modifiedTranslations!!)
    }

    if (dtoTags !== null && areTagsModified) {
      key.project.checkKeysEditPermission()
      tagService.updateTags(key, dtoTags)
    }

    if (isKeyModified) {
      key.project.checkKeysEditPermission()
    }

    if (isScreenshotAdded || isScreenshotDeleted) {
      updateScreenshotsWithPermissionCheck(dto, key)
    }

    return keyWithDataModelAssembler.toModel(keyService.edit(key, dto.name, dto.namespace))
  }

  private fun setActivityHolder() {
    if (!isOnlyOneOperation) {
      activityHolder.activity = ActivityType.COMPLEX_EDIT
      return
    }

    if (areTranslationsModified) {
      activityHolder.activity = ActivityType.SET_TRANSLATIONS
      return
    }

    if (areTagsModified) {
      activityHolder.activity = ActivityType.KEY_TAGS_EDIT
      return
    }

    if (isKeyModified) {
      activityHolder.activity = ActivityType.KEY_NAME_EDIT
      return
    }

    if (isScreenshotAdded) {
      activityHolder.activity = ActivityType.SCREENSHOT_ADD
      return
    }

    if (isScreenshotDeleted) {
      activityHolder.activity = ActivityType.SCREENSHOT_DELETE
      return
    }
  }

  private val isOnlyOneOperation: Boolean
    get() {
      return arrayOf(
        areTranslationsModified,
        areTagsModified,
        isKeyModified,
        isScreenshotAdded,
        isScreenshotDeleted
      ).sumOf { if (it) 1 as Int else 0 } == 0
    }

  private fun prepareData() {
    key = keyService.get(keyId)
    key.checkInProject()
    modifiedTranslations = dto.translations?.let { dtoTranslations -> filterModifiedOnly(dtoTranslations, key) }
  }

  private fun prepareConditions() {
    areTranslationsModified = !modifiedTranslations.isNullOrEmpty()
    areTagsModified = dtoTags != null && areTagsModified(key, dtoTags)
    isKeyModified = key.name != dto.name
    isScreenshotDeleted = !dto.screenshotIdsToDelete.isNullOrEmpty()
    isScreenshotAdded = !dto.screenshotUploadedImageIds.isNullOrEmpty() && !dto.screenshotsToAdd.isNullOrEmpty()
  }

  private fun areTagsModified(
    key: Key,
    dtoTags: List<String>
  ): Boolean {
    val existingTagsContainAllNewTags = key.keyMeta?.tags?.map { it.name }?.containsAll(dtoTags) ?: false
    val newTagsContainAllExistingTags = dtoTags.containsAll(key.keyMeta?.tags?.map { it.name } ?: listOf())

    return !existingTagsContainAllNewTags || !newTagsContainAllExistingTags
  }

  private fun filterModifiedOnly(
    dtoTranslations: Map<String, String?>,
    key: Key?
  ): Map<String, String?> {
    val languages = languageService.findByTags(dtoTranslations.keys, projectHolder.project.id)
    val existingTranslations = translationService.getKeyTranslations(
      languages,
      projectHolder.projectEntity,
      key
    ).associate { it.language.tag to it.text }

    return dtoTranslations.filter { it.value != existingTranslations[it.key] }
  }

  private fun updateScreenshotsWithPermissionCheck(dto: ComplexEditKeyDto, key: Key) {
    dto.screenshotIdsToDelete?.let { screenshotIds ->
      deleteScreenshots(screenshotIds, key)
    }

    if (isScreenshotAdded) {
      key.project.checkScreenshotsUploadPermission()
    }

    val screenshotUploadedImageIds = dto.screenshotUploadedImageIds
    if (screenshotUploadedImageIds != null) {
      screenshotService.saveUploadedImages(screenshotUploadedImageIds, key)
      return
    }

    val screenshotsToAdd = dto.screenshotsToAdd
    if (screenshotsToAdd != null) {
      screenshotService.saveUploadedImages(screenshotsToAdd, key)
    }
  }

  private fun deleteScreenshots(
    screenshotIds: List<Long>,
    key: Key
  ) {
    if (screenshotIds.isNotEmpty()) {
      key.project.checkScreenshotsDeletePermission()
    }
    screenshotService.findByIdIn(screenshotIds).forEach {
      screenshotService.removeScreenshotReference(key, it)
    }
  }

  private fun Project.checkScreenshotsDeletePermission() {
    if (authenticationFacade.isApiKeyAuthentication) {
      securityService.checkApiKeyScopes(setOf(ApiScope.SCREENSHOTS_DELETE), authenticationFacade.apiKey)
    }
    securityService.checkProjectPermission(this.id, Permission.ProjectPermissionType.TRANSLATE)
  }

  private fun Project.checkKeysEditPermission() {
    if (authenticationFacade.isApiKeyAuthentication) {
      securityService.checkApiKeyScopes(setOf(ApiScope.KEYS_EDIT), authenticationFacade.apiKey)
    }
    securityService.checkProjectPermission(this.id, Permission.ProjectPermissionType.EDIT)
  }

  private fun Key.checkInProject() {
    keyService.checkInProject(this, projectHolder.project.id)
  }

  private fun Project.checkScreenshotsUploadPermission() {
    securityService.checkScreenshotsUploadPermission(this.id)
  }
}
