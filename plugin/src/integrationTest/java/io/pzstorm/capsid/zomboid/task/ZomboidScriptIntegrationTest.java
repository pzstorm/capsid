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
package io.pzstorm.capsid.zomboid.task;

import java.io.File;
import java.io.IOException;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.initialization.GradlePropertiesController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginIntegrationTest;

class ZomboidScriptIntegrationTest extends PluginIntegrationTest {

	@Test
	void shouldCreateZomboidClassesAndSourcesDirectoryProperties() {

		ExtraPropertiesExtension ext = getProject(true).getExtensions().getExtraProperties();

		Assertions.assertTrue(ext.has("zomboidClassesDir"));
		Assertions.assertTrue(ext.has("zomboidSourcesDir"));
	}

	@Test
	void shouldCreateProjectZomboidConfigurations() {

		ConfigurationContainer configurations = getProject(true).getConfigurations();

		Assertions.assertDoesNotThrow(() -> configurations.getByName("zomboidRuntimeOnly"));
		Assertions.assertDoesNotThrow(() -> configurations.getByName("zomboidImplementation"));
	}

	@Test
	void shouldAddZomboidAssetsAndClassesDependencies() throws IOException {

		File gameDirFile = getGameDirPath().convert().toFile();

		File jarFile = new File(gameDirFile, "sample.jar");
		Assertions.assertTrue(jarFile.createNewFile());

		File otherFile = new File(gameDirFile, "otherFile.txt");
		Assertions.assertTrue(otherFile.createNewFile());

		Project project = getProject(true);
		ConfigurationContainer configurations = project.getConfigurations();

		Configuration zomboidRuntimeOnly = configurations.getByName("zomboidRuntimeOnly");
		/*
		 * workaround for "GradleProperties has not been loaded yet" issue
		 * https://github.com/gradle/gradle/issues/13122
		 */
		ProjectInternal projectInternal = (ProjectInternal) project;
		projectInternal.getServices().get(GradlePropertiesController.class)
				.loadGradlePropertiesFrom(project.getProjectDir());

		Assertions.assertTrue(zomboidRuntimeOnly.contains(jarFile));
		Assertions.assertFalse(zomboidRuntimeOnly.contains(otherFile));

		File mediaDir = new File(gameDirFile, "media").getAbsoluteFile();
		Assertions.assertTrue(mediaDir.exists());

		Configuration zomboidImplementation = configurations.getByName("zomboidImplementation");
		Assertions.assertTrue(zomboidImplementation.contains(mediaDir));
	}
}
