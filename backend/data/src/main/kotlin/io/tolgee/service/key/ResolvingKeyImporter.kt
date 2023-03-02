package io.tolgee.service.key

import io.tolgee.constants.Message
import io.tolgee.dtos.KeyImportResolvableResult
import io.tolgee.dtos.request.ScreenshotInfoDto
import io.tolgee.dtos.request.translation.importKeysResolvable.ImportKeysResolvableItemDto
import io.tolgee.dtos.request.translation.importKeysResolvable.ImportTranslationResolution
import io.tolgee.dtos.request.translation.importKeysResolvable.ImportTranslationResolvableDto
import io.tolgee.exceptions.BadRequestException
import io.tolgee.exceptions.NotFoundException
import io.tolgee.exceptions.PermissionException
import io.tolgee.model.Language
import io.tolgee.model.Project
import io.tolgee.model.Project_
import io.tolgee.model.Screenshot
import io.tolgee.model.UploadedImage
import io.tolgee.model.key.Key
import io.tolgee.model.key.Key_
import io.tolgee.model.key.Namespace
import io.tolgee.model.key.Namespace_
import io.tolgee.model.key.screenshotReference.KeyScreenshotReference
import io.tolgee.model.translation.Translation
import io.tolgee.security.AuthenticationFacade
import io.tolgee.service.ImageUploadService
import io.tolgee.service.LanguageService
import io.tolgee.service.translation.TranslationService
import io.tolgee.util.equalNullable
import org.springframework.context.ApplicationContext
import java.io.Serializable
import javax.persistence.EntityManager
import javax.persistence.criteria.Join
import javax.persistence.criteria.JoinType

class ResolvingKeyImporter(
  val applicationContext: ApplicationContext,
  val keysToImport: List<ImportKeysResolvableItemDto>,
  val projectEntity: Project
) {
  private val entityManager = applicationContext.getBean(EntityManager::class.java)
  private val keyService = applicationContext.getBean(KeyService::class.java)
  private val languageService = applicationContext.getBean(LanguageService::class.java)
  private val translationService = applicationContext.getBean(TranslationService::class.java)
  private val screenshotService = applicationContext.getBean(ScreenshotService::class.java)
  private val imageUploadService = applicationContext.getBean(ImageUploadService::class.java)
  private val authenticationFacade = applicationContext.getBean(AuthenticationFacade::class.java)

  private val errors = mutableListOf<List<Serializable?>>()
  private var importedKeys: List<Key> = emptyList()

  operator fun invoke(): KeyImportResolvableResult {
    importedKeys = tryImport()
    checkErrors()
    val screenshots = importScreenshots()
    return KeyImportResolvableResult(importedKeys, screenshots)
  }

  private fun tryImport(): List<Key> {
    return keysToImport.map keys@{ keyToImport ->
      val key = getOrCreateKey(keyToImport)

      keyToImport.mapLanguageAsKey().forEach translations@{ (language, resolvable) ->
        language ?: throw NotFoundException(Message.LANGUAGE_NOT_FOUND)
        val existingTranslation = getExistingTranslation(key, language)

        val isEmpty = existingTranslation !== null && existingTranslation.text.isNullOrEmpty()

        val isNew = existingTranslation == null

        val translationExists = !isEmpty && !isNew

        if (validate(translationExists, resolvable, key, language)) return@translations

        if (isEmpty || (!isNew && resolvable.resolution == ImportTranslationResolution.OVERRIDE)) {
          translationService.setTranslation(existingTranslation!!, resolvable.text)
          return@translations
        }

        if (isNew) {
          val translation = Translation(resolvable.text).apply {
            this.key = key
            this.language = language
          }
          translationService.save(translation)
        }
      }
      key
    }
  }

  private fun importScreenshots(): Map<Long, Screenshot> {
    val uploadedImagesIds = keysToImport.flatMap {
      it.screenshots?.map { screenshot -> screenshot.uploadedImageId } ?: listOf()
    }

    val images = imageUploadService.find(uploadedImagesIds)
    checkPermissions(images)

    val createdScreenshots = images.associate {
      it.id to screenshotService.saveScreenshot(it)
    }

    val locations = images.map { it.location }

    val allReferences = screenshotService.getKeyScreenshotReferences(
      importedKeys,
      locations
    ).toMutableList()

    val referencesToDelete = mutableListOf<KeyScreenshotReference>()

    keysToImport.forEach {
      val key = getOrCreateKey(it)
      it.screenshots?.forEach { screenshot ->
        val screenshotResult = createdScreenshots[screenshot.uploadedImageId]
          ?: throw NotFoundException(Message.ONE_OR_MORE_IMAGES_NOT_FOUND)
        val info = ScreenshotInfoDto(screenshot.text, screenshot.positions)

        screenshotService.addReference(
          key = key,
          screenshot = screenshotResult.screenshot,
          info = info,
          originalDimension = screenshotResult.originalDimension,
          targetDimension = screenshotResult.targetDimension
        )

        val toDelete = allReferences.filter { reference ->
          reference.key.id == key.id &&
            reference.screenshot.location == screenshotResult.screenshot.location
        }

        referencesToDelete.addAll(toDelete)
      }
    }

    screenshotService.removeScreenshotReferences(referencesToDelete)

    return createdScreenshots
      .map { (uploadedImageId, screenshotResult) ->
        uploadedImageId to screenshotResult.screenshot
      }.toMap()
  }

  private fun checkPermissions(images: List<UploadedImage>) {
    images.forEach { image ->
      if (authenticationFacade.userAccount.id != image.userAccount.id) {
        throw PermissionException()
      }
    }
  }

  private fun checkErrors() {
    if (errors.isNotEmpty()) {
      @Suppress("UNCHECKED_CAST")
      throw BadRequestException(Message.IMPORT_KEYS_ERROR, errors as List<Serializable>)
    }
  }

  private fun validate(
    translationExists: Boolean,
    resolvable: ImportTranslationResolvableDto,
    key: Key,
    language: Language
  ): Boolean {
    if (translationExists && resolvable.resolution == ImportTranslationResolution.NEW) {
      errors.add(
        listOf(Message.TRANSLATION_EXISTS.code, key.namespace?.name, key.name, language.tag)
      )
      return true
    }

    if (!translationExists && resolvable.resolution != ImportTranslationResolution.NEW) {
      errors.add(
        listOf(Message.TRANSLATION_NOT_FOUND.code, key.namespace?.name, key.name, language.tag)
      )
      return true
    }
    return false
  }

  private fun getExistingTranslation(
    key: Key,
    language: Language
  ) = existingTranslations[key.namespace?.name to key.name]?.get(language.tag)

  private fun ImportKeysResolvableItemDto.mapLanguageAsKey() =
    translations.mapNotNull { (languageTag, value) ->
      value ?: return@mapNotNull null
      languages[languageTag] to value
    }

  private fun getOrCreateKey(keyToImport: ImportKeysResolvableItemDto) =
    existingKeys.computeIfAbsent(keyToImport.namespace to keyToImport.name) {
      keyService.createWithoutExistenceCheck(
        name = keyToImport.name,
        namespace = keyToImport.namespace,
        project = projectEntity
      )
    }

  private fun getAllByNamespaceAndName(projectId: Long, keys: List<Pair<String?, String?>>): List<Key> {
    val cb = entityManager.criteriaBuilder
    val query = cb.createQuery(Key::class.java)
    val root = query.from(Key::class.java)

    @Suppress("UNCHECKED_CAST")
    val namespaceJoin: Join<Key, Namespace> = root.fetch(Key_.namespace, JoinType.LEFT) as Join<Key, Namespace>

    val predicates = keys.map { (namespace, name) ->
      cb.and(
        cb.equal(root.get(Key_.name), name),
        cb.equalNullable(namespaceJoin.get(Namespace_.name), namespace)
      )
    }.toTypedArray()

    val projectIdPath = root.get(Key_.project).get(Project_.id)

    query.where(cb.and(cb.equal(projectIdPath, projectId), cb.or(*predicates)))

    return this.entityManager.createQuery(query).resultList
  }

  private val existingKeys by lazy {
    this.getAllByNamespaceAndName(
      projectId = projectEntity.id,
      keys = keysToImport.map { it.namespace to it.name }
    ).associateBy { (it.namespace?.name to it.name) }.toMutableMap()
  }

  private val languages by lazy {
    val tags = keysToImport.flatMap { it.translations.keys }.toSet()
    languageService.findByTags(tags, projectEntity.id).associateBy { it.tag }
  }

  private val keyLanguagesMap by lazy {
    keysToImport.mapNotNull {
      val key = existingKeys[it.namespace to it.name] ?: return@mapNotNull null
      val keyLanguages = it.translations.keys.mapNotNull { tag -> languages[tag] }
      key to keyLanguages
    }.toMap()
  }

  private val existingTranslations by lazy {
    translationService.get(keyLanguagesMap)
      .groupBy { it.key.namespace?.name to it.key.name }.map { (key, translations) ->
        key to translations.associateBy { it.language.tag }
      }.toMap()
  }
}
