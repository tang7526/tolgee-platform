package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the `libs` extension.
*/
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(providers, config);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers) {
        super(config, providers);
    }

        /**
         * Creates a dependency provider for amazonS3 (com.amazonaws:aws-java-sdk-s3)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getAmazonS3() { return create("amazonS3"); }

        /**
         * Creates a dependency provider for amazonTranslate (com.amazonaws:aws-java-sdk-translate)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getAmazonTranslate() { return create("amazonTranslate"); }

        /**
         * Creates a dependency provider for assertJCore (org.assertj:assertj-core)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getAssertJCore() { return create("assertJCore"); }

        /**
         * Creates a dependency provider for commonsCodec (commons-codec:commons-codec)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getCommonsCodec() { return create("commonsCodec"); }

        /**
         * Creates a dependency provider for googleCloud (com.google.cloud:libraries-bom)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getGoogleCloud() { return create("googleCloud"); }

        /**
         * Creates a dependency provider for icu4j (com.ibm.icu:icu4j)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getIcu4j() { return create("icu4j"); }

        /**
         * Creates a dependency provider for jacksonModuleKotlin (com.fasterxml.jackson.module:jackson-module-kotlin)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getJacksonModuleKotlin() { return create("jacksonModuleKotlin"); }

        /**
         * Creates a dependency provider for jjwtApi (io.jsonwebtoken:jjwt-api)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getJjwtApi() { return create("jjwtApi"); }

        /**
         * Creates a dependency provider for jjwtImpl (io.jsonwebtoken:jjwt-impl)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getJjwtImpl() { return create("jjwtImpl"); }

        /**
         * Creates a dependency provider for jjwtJackson (io.jsonwebtoken:jjwt-jackson)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getJjwtJackson() { return create("jjwtJackson"); }

        /**
         * Creates a dependency provider for jsonUnitAssert (net.javacrumbs.json-unit:json-unit-assertj)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getJsonUnitAssert() { return create("jsonUnitAssert"); }

        /**
         * Creates a dependency provider for kotlin (org.jetbrains.kotlin:kotlin-gradle-plugin)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getKotlin() { return create("kotlin"); }

        /**
         * Creates a dependency provider for kotlinCoroutines (org.jetbrains.kotlinx:kotlinx-coroutines-core)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getKotlinCoroutines() { return create("kotlinCoroutines"); }

        /**
         * Creates a dependency provider for kotlinReflect (org.jetbrains.kotlin:kotlin-reflect)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getKotlinReflect() { return create("kotlinReflect"); }

        /**
         * Creates a dependency provider for liquibaseCore (org.liquibase:liquibase-core)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getLiquibaseCore() { return create("liquibaseCore"); }

        /**
         * Creates a dependency provider for liquibaseHibernate (org.liquibase.ext:liquibase-hibernate5)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getLiquibaseHibernate() { return create("liquibaseHibernate"); }

        /**
         * Creates a dependency provider for mockito (org.mockito.kotlin:mockito-kotlin)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getMockito() { return create("mockito"); }

        /**
         * Creates a dependency provider for sendInBlue (com.sendinblue:sib-api-v3-sdk)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getSendInBlue() { return create("sendInBlue"); }

        /**
         * Creates a dependency provider for sentry (io.sentry:sentry-spring-boot-starter)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getSentry() { return create("sentry"); }

        /**
         * Creates a dependency provider for springDocOpenApiDataRest (org.springdoc:springdoc-openapi-data-rest)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getSpringDocOpenApiDataRest() { return create("springDocOpenApiDataRest"); }

        /**
         * Creates a dependency provider for springDocOpenApiHateoas (org.springdoc:springdoc-openapi-hateoas)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getSpringDocOpenApiHateoas() { return create("springDocOpenApiHateoas"); }

        /**
         * Creates a dependency provider for springDocOpenApiKotlin (org.springdoc:springdoc-openapi-kotlin)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getSpringDocOpenApiKotlin() { return create("springDocOpenApiKotlin"); }

        /**
         * Creates a dependency provider for springDocOpenApiUi (org.springdoc:springdoc-openapi-ui)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getSpringDocOpenApiUi() { return create("springDocOpenApiUi"); }

        /**
         * Creates a dependency provider for springDocOpenApiWebMvcCore (org.springdoc:springdoc-openapi-webmvc-core)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getSpringDocOpenApiWebMvcCore() { return create("springDocOpenApiWebMvcCore"); }

        /**
         * Creates a dependency provider for springmockk (com.ninja-squad:springmockk)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getSpringmockk() { return create("springmockk"); }

        /**
         * Creates a dependency provider for stripe (com.stripe:stripe-java)
         * This dependency was declared in settings file 'settings.gradle'
         */
        public Provider<MinimalExternalModuleDependency> getStripe() { return create("stripe"); }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() { return vaccForVersionAccessors; }

    /**
     * Returns the group of bundles at bundles
     */
    public BundleAccessors getBundles() { return baccForBundleAccessors; }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() { return paccForPluginAccessors; }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
