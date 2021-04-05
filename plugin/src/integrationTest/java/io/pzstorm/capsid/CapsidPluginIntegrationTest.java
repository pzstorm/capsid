/*
 * Storm Capsid - Project Zomboid mod development framework for Gradle.
 * Copyright (C) 2021 Matthew Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.pzstorm.capsid;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.jvm.toolchain.JavaToolchainSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("UnstableApiUsage")
class CapsidPluginIntegrationTest extends PluginIntegrationTest {

	@Test
	void shouldApplyAllCorePlugins() {

		PluginContainer plugins = getProject(true).getPlugins();
		for (CorePlugin plugin : CorePlugin.values()) {
			Assertions.assertTrue(plugins.hasPlugin(plugin.getID()));
		}
	}

	@Test
	void shouldRegisterAllRepositories() {

		RepositoryHandler repositories = getProject(true).getRepositories();
		Assertions.assertEquals(2, repositories.size());
		Assertions.assertNotNull(repositories.findByName("MavenRepo"));
	}

	@Test
	void shouldConfigureJavaToolchainLanguageLevel() {

		JavaPluginExtension java = Objects.requireNonNull(
				getProject(true).getExtensions().getByType(JavaPluginExtension.class)
		);
		JavaToolchainSpec toolchain = java.getToolchain();
		Assertions.assertEquals(8, toolchain.getLanguageVersion().get().asInt());
	}

	@Test
	void shouldCreateCustomSourceSetsWithSourceDirs() {

		Project project = getProject(true);
		Convention convention = project.getConvention();
		JavaPluginConvention javaPlugin = convention.getPlugin(JavaPluginConvention.class);
		SourceSetContainer sourceSets = javaPlugin.getSourceSets();

		SourceSet mediaSourceSet = sourceSets.getByName("media");
		Set<File> sourceDirs = mediaSourceSet.getJava().getSrcDirs();

		Path expectedPath = project.getProjectDir().toPath().resolve("media/lua");
		Assertions.assertTrue(sourceDirs.stream().anyMatch(d -> d.toPath().equals(expectedPath)));
	}
}
