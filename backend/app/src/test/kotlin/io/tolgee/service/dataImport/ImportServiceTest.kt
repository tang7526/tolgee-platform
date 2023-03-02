package io.tolgee.service.dataImport

import io.tolgee.AbstractSpringTest
import io.tolgee.development.testDataBuilder.data.dataImport.ImportNamespacesTestData
import io.tolgee.development.testDataBuilder.data.dataImport.ImportTestData
import io.tolgee.testing.assert
import io.tolgee.testing.assertions.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class ImportServiceTest : AbstractSpringTest() {
  lateinit var importTestData: ImportTestData

  @BeforeEach
  fun setup() {
    importTestData = ImportTestData()
    importTestData.addFrenchTranslations()
  }

  @Test
  fun `it selects existing language`() {
    executeInNewTransaction {
      importTestData.setAllResolved()
      testDataService.saveTestData(importTestData.root)
    }
    executeInNewTransaction {
      val importFrench = importService.findLanguage(importTestData.importFrench.id)!!
      val french = languageService.get(importTestData.french.id)
      importService.selectExistingLanguage(importFrench, french)
      assertThat(importFrench.existingLanguage).isEqualTo(french)
      val translations = importService.findTranslations(importTestData.importFrench.id)
      assertThat(translations[0].conflict).isNotNull
      assertThat(translations[1].conflict).isNull()
    }
  }

  @Test
  fun `deletes import language`() {
    val testData = executeInNewTransaction {
      val testData = ImportTestData()
      testDataService.saveTestData(testData.root)
      assertThat(importService.findLanguage(testData.importEnglish.id)).isNotNull
      testData
    }
    executeInNewTransaction {
      importService.findLanguage(testData.importEnglish.id)?.let {
        importService.deleteLanguage(it)
      }
      entityManager.flush()
      entityManager.clear()
    }
    executeInNewTransaction {
      assertThat(importService.findLanguage(testData.importEnglish.id)).isNull()
    }
  }

  @Test
  fun `deletes import`() {
    val testData = ImportTestData()
    executeInNewTransaction {
      testData.addFileIssues()
      testData.addKeyMetadata()
      testDataService.saveTestData(testData.root)
    }

    executeInNewTransaction {
      val import = importService.get(testData.import.id)
      importService.deleteImport(import)
    }

    executeInNewTransaction {
      assertThat(importService.find(testData.import.project.id, testData.import.author.id)).isNull()
    }
  }

  @Test
  fun `imports namespaces and merges same keys from multiple files`() {
    val testData = executeInNewTransaction {
      val testData = ImportNamespacesTestData()
      testDataService.saveTestData(testData.root)
      testData
    }
    executeInNewTransaction {
      val import = importService.get(testData.import.id)
      importService.import(import)
    }
    executeInNewTransaction {
      keyService.find(testData.project.id, "what a key", "homepage").assert.isNotNull
      val whatAKey = keyService.find(testData.project.id, "what a key", null)
      whatAKey!!.keyMeta!!.comments.assert.hasSize(2).anyMatch { it.text == "hello1" }.anyMatch { it.text == "hello2" }
    }
  }
}
