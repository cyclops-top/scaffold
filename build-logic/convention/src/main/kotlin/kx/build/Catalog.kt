@file:Suppress("unused")

package kx.build

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugin.use.PluginDependency

fun Project.libs(): Lazy<VersionCatalog> {
    return lazy {
        extensions.getByType<VersionCatalogsExtension>().named("libs")
    }
}

fun VersionCatalog.version(alias: String): String {
    return findVersion(alias).get().toString()
}

fun VersionCatalog.bundle(alias: String): Provider<ExternalModuleDependencyBundle> {
    return findBundle(alias).get()
}

fun VersionCatalog.library(alias: String): Provider<MinimalExternalModuleDependency> {
    return findLibrary(alias).get()
}

fun VersionCatalog.plugin(alias: String): Provider<PluginDependency> {
    return findPlugin(alias).get()
}