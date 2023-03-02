package io.tolgee.model

import io.tolgee.activity.annotation.ActivityLoggedProp
import io.tolgee.model.key.Key
import io.tolgee.model.key.Namespace
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.OrderBy
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["address_part"], name = "project_address_part_unique")])
@EntityListeners(Project.Companion.ProjectListener::class)
class Project(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  override var id: Long = 0L,

  @field:NotBlank
  @field:Size(min = 3, max = 50)
  @ActivityLoggedProp
  var name: String = "",

  @field:Size(min = 3, max = 2000)
  @ActivityLoggedProp
  var description: String? = null,

  @Column(name = "address_part")
  @ActivityLoggedProp
  @field:Size(min = 3, max = 60)
  @field:Pattern(regexp = "^[a-z0-9-]*[a-z]+[a-z0-9-]*$", message = "invalid_pattern")
  var slug: String? = null,
) : AuditModel(), ModelWithAvatar, EntityWithId {

  @OrderBy("id")
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
  var languages: MutableSet<Language> = LinkedHashSet()

  @OneToMany(mappedBy = "project")
  var permissions: MutableSet<Permission> = LinkedHashSet()

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
  var keys: MutableList<Key> = mutableListOf()

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
  var apiKeys: MutableSet<ApiKey> = LinkedHashSet()

  @Suppress("SetterBackingFieldAssignment")
  @ManyToOne(optional = true, fetch = FetchType.LAZY)
  @Deprecated(message = "Project can be owned only by organization")
  var userOwner: UserAccount? = null

  @ManyToOne(optional = true)
  lateinit var organizationOwner: Organization

  @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
  @ActivityLoggedProp
  var baseLanguage: Language? = null

  @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], mappedBy = "project")
  var autoTranslationConfig: AutoTranslationConfig? = null

  @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "project")
  var mtServiceConfig: MutableList<MtServiceConfig> = mutableListOf()

  @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "project")
  var namespaces: MutableList<Namespace> = mutableListOf()

  @ActivityLoggedProp
  override var avatarHash: String? = null

  @Transient
  override var disableActivityLogging = false

  constructor(name: String, description: String? = null, slug: String?, organizationOwner: Organization) :
    this(id = 0L, name, description, slug) {
      this.organizationOwner = organizationOwner
    }

  fun getLanguage(tag: String): Optional<Language> {
    return languages.stream().filter { l: Language -> (l.tag == tag) }.findFirst()
  }

  companion object {
    class ProjectListener {
      @PrePersist
      @PreUpdate
      fun preSave(project: Project) {
        if (!(!project::organizationOwner.isInitialized).xor(project.userOwner == null)) {
          throw Exception("Exactly one of organizationOwner or userOwner must be set!")
        }
      }
    }
  }
}
