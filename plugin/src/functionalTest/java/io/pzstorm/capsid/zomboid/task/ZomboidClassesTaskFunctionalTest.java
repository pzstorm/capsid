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
import java.nio.file.Files;
import java.util.*;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.zomboid.ZomboidTasks;

class ZomboidClassesTaskFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldSyncZomboidClassesFromInstallDirectory() throws IOException {

		File zomboidClassesDir = ProjectProperty.ZOMBOID_CLASSES_DIR.get(getProject());
		Assertions.assertFalse(zomboidClassesDir.exists());

		File gameDir = getGameDirPath().convert().toAbsolutePath().toFile();
		File[] includedFiles = new File[] {
				new File(gameDir, "incFile1.class"),
				new File(gameDir, "incFile2.class"),
				new File(gameDir, "incFile3.class"),
				new File(gameDir, "stdlib.lbc")
		};
		for (File file : includedFiles) {
			Assertions.assertTrue(file.createNewFile());
		}
		File[] excludedFiles = new File[] {
				new File(gameDir, "excFile1.txt"),
				new File(gameDir, "excFile2.png")
		};
		for (File file : excludedFiles) {
			Assertions.assertTrue(file.createNewFile());
		}
		File excludedDir = new File(gameDir, "excludedDir");
		Files.createDirectory(excludedDir.toPath());

		GradleRunner runner = getRunner();
		List<String> arguments = new ArrayList<>(runner.getArguments());
		arguments.add(ZomboidTasks.ZOMBOID_CLASSES.name);

		BuildResult result = getRunner().withArguments(arguments).build();
		assertTaskOutcomeSuccess(result, ZomboidTasks.ZOMBOID_CLASSES.name);

		// class files that were synced from install directory
		Set<File> zomboidClasses = new HashSet<>(Arrays.asList(zomboidClassesDir.listFiles()));
		Assertions.assertEquals(includedFiles.length, zomboidClasses.size());

		// expect finding all included files
		for (File includedFile : includedFiles)
		{
			File targetFile = new File(zomboidClassesDir, includedFile.getName());
			Assertions.assertTrue(zomboidClasses.contains(targetFile));
		}
		// expect NOT finding any excluded files
		for (File excludedFile : excludedFiles)
		{
			File targetFile = new File(zomboidClassesDir, excludedFile.getName());
			Assertions.assertFalse(zomboidClasses.contains(targetFile));
		}
		// expect NOT finding excluded directory
		Assertions.assertFalse(zomboidClasses.contains(excludedDir));
	}
}
