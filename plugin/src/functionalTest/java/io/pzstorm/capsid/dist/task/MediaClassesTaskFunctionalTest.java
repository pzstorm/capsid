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
package io.pzstorm.capsid.dist.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.dist.DistributionTasks;

class MediaClassesTaskFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldAssembleMediaClassesWithCorrectDirectoryStructure() throws IOException {

		String[] filesToCreate = new String[]{
				"lua/client/mainClient.lua",
				"lua/server/mainServer.lua",
				"models/testModel.obj",
				"maps/testMap.map"
		};
		for (String path : filesToCreate)
		{
			File file = getProject().file("media/" + path);
			File parentFile = file.getParentFile();
			Assertions.assertTrue(parentFile.exists() || parentFile.mkdirs());
			Assertions.assertTrue(file.createNewFile());
		}
		BuildResult result = getRunner().withArguments(DistributionTasks.MEDIA_CLASSES.name).build();
		assertTaskOutcomeSuccess(result, DistributionTasks.MEDIA_CLASSES.name);

		File mediaClassesDir = ProjectProperty.MEDIA_CLASSES_DIR.get(getProject());
		String[] expectedFiles = new String[]{
				"lua/client/mainClient.lua",
				"lua/server/mainServer.lua"
		};
		long filesFound = Files.walk(mediaClassesDir.toPath()).filter(Files::isRegularFile).count();
		Assertions.assertEquals(expectedFiles.length, filesFound);

		for (String expectedFile : expectedFiles) {
			Assertions.assertTrue(new File(mediaClassesDir, expectedFile).exists());
		}
	}
}
