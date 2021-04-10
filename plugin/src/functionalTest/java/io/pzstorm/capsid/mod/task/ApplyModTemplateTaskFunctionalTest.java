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
package io.pzstorm.capsid.mod.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.util.GFileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.mod.ModTasks;
import io.pzstorm.capsid.util.Utils;

class ApplyModTemplateTaskFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldApplyModTemplateToProjectRootDirectory() throws IOException {

		File templateDir = new File(getProjectDir(), "docs/template");
		File templateResourceDir = Utils.getFileFromResources("docs/template");

		File[] expectedFiles = templateResourceDir.listFiles();
		Assertions.assertEquals(2, Objects.requireNonNull(expectedFiles).length);

		// assert that no files are present in template directory
		Assertions.assertNull(templateDir.listFiles());

		// create directory structure before copying directory
		Assertions.assertTrue(templateDir.getParentFile().mkdirs());

		GFileUtils.copyDirectory(templateResourceDir, templateDir);
		File[] actualFiles = templateDir.listFiles();

		Assertions.assertEquals(expectedFiles.length, Objects.requireNonNull(actualFiles).length);
		for (int i = 0; i < actualFiles.length; i++)
		{
			// compare file bytes to make sure they are identical
			com.google.common.io.Files.asByteSource(actualFiles[i]).contentEquals(
					com.google.common.io.Files.asByteSource(expectedFiles[i]));
		}
		File[] expectedAppliedFiles = new File[expectedFiles.length];
		for (int i = 0; i < expectedFiles.length; i++)
		{
			File file = new File(getProjectDir(), expectedFiles[i].getName());
			expectedAppliedFiles[i] = file;

			// assert that mod template has not been applied yet
			Assertions.assertFalse(file.exists());
		}
		GradleRunner runner = getRunner();
		List<String> arguments = new ArrayList<>(runner.getArguments());
		arguments.add(ModTasks.APPLY_MOD_TEMPLATE.name);

		BuildResult result = runner.withArguments(arguments).build();
		assertTaskOutcomeSuccess(result, ModTasks.APPLY_MOD_TEMPLATE.name);

		// assert that expected files are applied
		for (File expectedAppliedFile : expectedAppliedFiles) {
			Assertions.assertTrue(expectedAppliedFile.exists());
		}
	}
}
