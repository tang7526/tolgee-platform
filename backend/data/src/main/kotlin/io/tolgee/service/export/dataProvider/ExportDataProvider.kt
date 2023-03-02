package io.tolgee.service.export.dataProvider

import io.tolgee.dtos.request.export.ExportParams
import io.tolgee.model.Language
import io.tolgee.model.Language_
import io.tolgee.model.Project
import io.tolgee.model.Project_
import io.tolgee.model.enums.TranslationState
import io.tolgee.model.key.Key
import io.tolgee.model.key.KeyMeta
import io.tolgee.model.key.KeyMeta_
import io.tolgee.model.key.Key_
import io.tolgee.model.key.Namespace
import io.tolgee.model.key.Namespace_
import io.tolgee.model.key.Tag
import io.tolgee.model.key.Tag_
import io.tolgee.model.translation.Translation
import io.tolgee.model.translation.Translation_
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Join
import javax.persistence.criteria.JoinType
import javax.persistence.criteria.ListJoin
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import javax.persistence.criteria.SetJoin

class ExportDataProvider(
  private val entityManager: EntityManager,
  private val exportParams: ExportParams,
  private val projectId: Long
) {
  private val cb: CriteriaBuilder = entityManager.criteriaBuilder
  val query: CriteriaQuery<ExportDataView> = cb.createQuery(ExportDataView::class.java)

  private var key: Root<Key> = query.from(Key::class.java)
  private lateinit var tagJoin: SetJoin<KeyMeta, Tag>
  private lateinit var keyMetaJoin: Join<Key, KeyMeta>
  private lateinit var translationJoin: ListJoin<Key, Translation>
  private lateinit var languageJoin: SetJoin<Project, Language>
  private lateinit var projectJoin: Join<Key, Project>
  private lateinit var namespaceJoin: Join<Key, Namespace>

  private var whereConditions: MutableList<Predicate> = mutableListOf()

  init {
    initQuery()
  }

  private fun initQuery() {
    addJoins()
    addWhereConditions()
    addSelect()
  }

  private fun addJoins() {
    projectJoin = key.join(Key_.project)
    namespaceJoin = key.join(Key_.namespace, JoinType.LEFT)
    languageJoin = joinLanguage()
    translationJoin = joinTranslation(key, languageJoin)
    keyMetaJoin = key.join(Key_.keyMeta, JoinType.LEFT)
    tagJoin = keyMetaJoin.join(KeyMeta_.tags, JoinType.LEFT)
  }

  private fun addSelect() {
    query.multiselect(
      key.get(Key_.id),
      key.get(Key_.name),
      namespaceJoin.get(Namespace_.name),
      languageJoin.get(Language_.id),
      languageJoin.get(Language_.tag),
      translationJoin.get(Translation_.id),
      translationJoin.get(Translation_.text),
      translationJoin.get(Translation_.state),
    )
  }

  private fun addWhereConditions() {
    filterProjectId()
    filterTag()
    filterKeyId()
    filterKeyIdNot()
    filterKeyPrefix()
    filterState()
    filterNamespaces()

    query.where(*whereConditions.toTypedArray())
    query.orderBy(cb.asc(key.get(Key_.name)))
  }

  private fun filterProjectId() {
    whereConditions.add(cb.equal(projectJoin.get(Project_.id), projectId))
  }

  private fun filterTag() {
    if (exportParams.filterTag != null) {
      whereConditions.add(tagJoin.get(Tag_.name).`in`(exportParams.filterTag))
    }
  }

  private fun filterKeyId() {
    if (exportParams.filterKeyId != null) {
      whereConditions.add(key.get(Key_.id).`in`(exportParams.filterKeyId))
    }
  }

  private fun filterKeyIdNot() {
    if (exportParams.filterKeyIdNot != null) {
      whereConditions.add(cb.not(key.get(Key_.id).`in`(exportParams.filterKeyIdNot)))
    }
  }

  private fun filterKeyPrefix() {
    if (exportParams.filterKeyPrefix != null) {
      whereConditions.add(cb.like(key.get(Key_.name), "${exportParams.filterKeyPrefix}%"))
    }
  }

  private fun filterState() {
    if (exportParams.filterState != null) {
      var condition = translationJoin.get(Translation_.state).`in`(exportParams.filterState)

      if (exportParams.shouldContainUntranslated) {
        condition = cb.or(condition, cb.isNull(translationJoin))
      }

      whereConditions.add(condition)
    }
  }

  private fun filterNamespaces() {
    val filterNamespace = exportParams.filterNamespace
    if (!filterNamespace.isNullOrEmpty()) {
      val hasNull = filterNamespace.contains(null) || filterNamespace.contains("")

      val withoutNull = filterNamespace.filterNotNull()
      val namespaceName = namespaceJoin.get(Namespace_.name)
      var condition = namespaceName.`in`(withoutNull)
      if (hasNull) {
        condition = cb.or(condition, namespaceName.isNull)
      }
      whereConditions.add(condition)
    }
  }

  private fun joinTranslation(
    key: Root<Key>,
    language: SetJoin<Project, Language>
  ): ListJoin<Key, Translation> {
    val translation = key.join(Key_.translations, JoinType.LEFT)
    translation.on(
      cb.and(
        cb.equal(key, translation.get(Translation_.key)),
        cb.equal(language, translation.get(Translation_.language))
      )
    )
    return translation
  }

  private fun joinLanguage(): SetJoin<Project, Language> {
    val language = projectJoin.join(Project_.languages)

    if (exportParams.languages != null) {
      language.on(language.get(Language_.tag).`in`(exportParams.languages))
    }
    return language
  }

  fun getData(): List<ExportTranslationView> {
    val resultList = entityManager.createQuery(query).resultList
    val keyMap = transformResult(resultList)
    return keyMap.flatMap { it.value.translations.values }
  }

  private fun transformResult(resultList: MutableList<ExportDataView>): HashMap<Long, ExportKeyView> {
    val keyMap = LinkedHashMap<Long, ExportKeyView>()
    resultList.forEach { dataView ->
      val keyView = keyMap.computeIfAbsent(dataView.keyId) {
        ExportKeyView(dataView.keyId, dataView.keyName, dataView.namespace)
      }

      keyView.translations[dataView.languageTag] = ExportTranslationView(
        id = dataView.translationId,
        text = dataView.translationText,
        state = dataView.translationState ?: TranslationState.UNTRANSLATED,
        key = keyView,
        languageTag = dataView.languageTag
      )
    }
    return keyMap
  }
}
