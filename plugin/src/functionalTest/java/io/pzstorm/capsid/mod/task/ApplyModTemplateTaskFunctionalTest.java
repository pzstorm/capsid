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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.base.Splitter;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.mod.ModTasks;
import io.pzstorm.capsid.util.Utils;

class ApplyModTemplateTaskFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldApplyModTemplateToProjectRootDirectory() throws IOException {

		List<File> expectedFiles = new ArrayList<>();
		Splitter.on('\n').splitToList(
				Utils.readResourceAsTextFromStream(CapsidPlugin.class, "template/template.txt")
		).forEach(path -> {
			String relPath = Paths.get("template").relativize(Paths.get(path)).toString();
			expectedFiles.add(new File(getProjectDir(), relPath));
		});
		for (File expectedFile : expectedFiles) {
			Assertions.assertFalse(expectedFile.exists());
		}
		GradleRunner runner = getRunner();
		List<String> arguments = new ArrayList<>(runner.getArguments());
		arguments.add(ModTasks.APPLY_MOD_TEMPLATE.name);

		BuildResult result = runner.withArguments(arguments).build();
		assertTaskOutcomeSuccess(result, ModTasks.APPLY_MOD_TEMPLATE.name);

		for (File expectedFile : expectedFiles) {
			Assertions.assertTrue(expectedFile.exists());
		}
	}
}
