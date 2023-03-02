package io.tolgee.repository

import io.tolgee.model.key.Key
import io.tolgee.service.key.KeySearchResultView
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface KeyRepository : JpaRepository<Key, Long> {

  @Query(
    """
    from Key k
    left join k.namespace ns
    where 
      k.name = :name 
      and k.project.id = :projectId 
      and (ns.name = :namespace or (ns is null and :namespace is null))
  """
  )
  fun getByNameAndNamespace(projectId: Long, name: String, namespace: String?): Optional<Key>

  @Query(
    """
      from Key k left join fetch k.namespace left join fetch k.keyMeta where k.project.id = :projectId
    """
  )
  fun getAllByProjectId(projectId: Long): Set<Key>

  @Query(
    """
      from Key k left join fetch k.namespace where k.project.id = :projectId order by k.id
    """
  )
  fun getAllByProjectIdSortedById(projectId: Long): List<Key>

  @Query("select k.id from Key k where k.project.id = :projectId")
  fun getIdsByProjectId(projectId: Long?): List<Long>
  fun deleteAllByIdIn(ids: Collection<Long>)
  fun findAllByIdIn(ids: Collection<Long>): List<Key>

  @Query("from Key k join fetch k.project left join fetch k.keyMeta where k.id in :ids")
  fun findWithProjectsAndMetas(ids: Set<Long>): List<Key>

  @Query(
    """
    select k.id as id, ns.name as namespace, k.name as name, bt.text as baseTranslation, t.text as translation from 
       key k
       join project p on p.id = k.project_id and p.id = :projectId
       left join namespace ns on k.namespace_id = ns.id
       left join language l on p.id = l.project_id and l.tag = :languageTag
       left join translation bt on bt.key_id = k.id and (bt.language_id = p.base_language_id)
       left join translation t on t.key_id = k.id and (t.language_id = l.id),
    lower(f_unaccent(:search)) as searchUnaccent
    where (
          lower(f_unaccent(ns.name)) %> searchUnaccent
          or lower(f_unaccent(k.name)) %> searchUnaccent
          or lower(f_unaccent(t.text)) %> searchUnaccent
          or lower(f_unaccent(bt.text)) %> searchUnaccent
          )
       order by 
       (
       3 * (ns.name <-> searchUnaccent) + 
       3 * (k.name <-> searchUnaccent) + 
       (t.text <-> searchUnaccent) +
       (bt.text <-> searchUnaccent)
       ) desc, k.id
    limit :#{#pageable.pageSize}
    offset :#{#pageable.offset}
  """,
    nativeQuery = true,
    countQuery = """
      select count(k.id) 
      from key k
       join project p on p.id = k.project_id and p.id = :projectId
       left join namespace ns on k.namespace_id = ns.id
       left join language l on p.id = l.project_id and l.tag = :languageTag
       left join translation bt on bt.key_id = k.id and (bt.language_id = p.base_language_id)
       left join translation t on t.key_id = k.id and (t.language_id = l.id),
      lower(f_unaccent(:search)) as searchUnaccent  
      where (
          lower(f_unaccent(ns.name)) %> searchUnaccent
          or lower(f_unaccent(k.name)) %> searchUnaccent
          or lower(f_unaccent(t.text)) %> searchUnaccent
          or lower(f_unaccent(bt.text)) %> searchUnaccent
          )
      """
  )
  fun searchKeys(search: String, projectId: Long, languageTag: String?, pageable: Pageable): Page<KeySearchResultView>

  @Query(
    """
      select k from Key k 
      left join fetch k.namespace where k.project.id = :projectId
    """,
    countQuery = """
      select count(k) from Key k 
      where k.project.id = :projectId
    """
  )
  fun getAllByProjectId(projectId: Long, pageable: Pageable): Page<Key>

  @Query(
    """
    select k from Key k
    left join fetch k.keyMeta km
    left join fetch km.tags
    where k in :keys
  """
  )
  fun getWithTags(keys: Set<Key>): List<Key>
}
