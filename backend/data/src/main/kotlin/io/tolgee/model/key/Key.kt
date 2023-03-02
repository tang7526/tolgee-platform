package io.tolgee.model.key

import io.tolgee.activity.annotation.ActivityDescribingProp
import io.tolgee.activity.annotation.ActivityEntityDescribingPaths
import io.tolgee.activity.annotation.ActivityLoggedEntity
import io.tolgee.activity.annotation.ActivityLoggedProp
import io.tolgee.activity.annotation.ActivityReturnsExistence
import io.tolgee.dtos.PathDTO
import io.tolgee.events.OnKeyPrePersist
import io.tolgee.events.OnKeyPreRemove
import io.tolgee.model.Project
import io.tolgee.model.StandardAuditModel
import io.tolgee.model.dataImport.WithKeyMeta
import io.tolgee.model.key.screenshotReference.KeyScreenshotReference
import io.tolgee.model.translation.Translation
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.ApplicationEventPublisher
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.PrePersist
import javax.persistence.PreRemove
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@ActivityLoggedEntity
@ActivityReturnsExistence
@ActivityEntityDescribingPaths(["namespace"])
@EntityListeners(Key.Companion.KeyListeners::class)
class Key(
  @field:NotBlank
  @field:Size(max = 2000)
  @field:Column(length = 2000)
  @ActivityLoggedProp
  @ActivityDescribingProp
  var name: String = "",
) : StandardAuditModel(), WithKeyMeta {
  @field:NotNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  lateinit var project: Project

  @ManyToOne
  @ActivityLoggedProp
  var namespace: Namespace? = null

  @OneToMany(mappedBy = "key")
  var translations: MutableList<Translation> = mutableListOf()

  @OneToOne(mappedBy = "key", fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
  override var keyMeta: KeyMeta? = null

  @OneToMany(mappedBy = "key", orphanRemoval = true)
  var keyScreenshotReferences: MutableList<KeyScreenshotReference> = mutableListOf()

  constructor(
    name: String,
    project: Project,
    translations: MutableList<Translation> = mutableListOf()
  ) : this(name) {
    this.project = project
    this.translations = translations
  }

  val path: PathDTO
    get() = PathDTO.fromFullPath(name)

  companion object {
    @Configurable
    class KeyListeners {
      @Autowired
      lateinit var eventPublisherProvider: ObjectFactory<ApplicationEventPublisher>

      @PrePersist
      fun prePersist(key: Key) {
        eventPublisherProvider.`object`.publishEvent(OnKeyPrePersist(source = this, key))
      }

      @PreRemove
      fun preRemove(key: Key) {
        eventPublisherProvider.`object`.publishEvent(OnKeyPreRemove(source = this, key))
      }
    }
  }

  override fun hashCode(): Int {
    return id.hashCode() * name.hashCode()
  }
}
