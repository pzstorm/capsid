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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.mod.ModTasks;

class ShowModMetadataTaskFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldPrintMetadataInformationToStreamOutput() throws IOException {

		GradleRunner runner = getRunner();
		writeToProjectFile("mod.info", new String[]{
				"name=TestMod",
				"description=",
				"url=https://github.com/test/mod",
				"id=" + runner.getProjectDir().getName(),
				"modversion=1.0.5",
				"pzversion=41.50-IWBUMS"
		});
		BuildResult result = runner.withArguments(ModTasks.SHOW_MOD_METADATA.name).build();
		assertTaskOutcomeSuccess(result, ModTasks.SHOW_MOD_METADATA.name);

		List<String> expectedOutput = ImmutableList.of(
				"This is a mod for Project Zomboid 41.50-IWBUMS",
				"------------------------------------------------",
				"Name: TestMod",
				"Description: <not specified>",
				"URL: https://github.com/test/mod",
				"ID: " + runner.getProjectDir().getName(),
				"Version: 1.0.5"
		);
		List<String> actualOutput = new ArrayList<>(
				Splitter.on(System.lineSeparator()).splitToList(result.getOutput())
		);
		// remove lines from the ending of file
		for (int i = 4; i > 0; i--) {
			actualOutput.remove(actualOutput.size() - 1);
		}
		// remove lines from the beginning of file
		for (int i = 2; i > 0; i--) {
			actualOutput.remove(0);
		}
		Assertions.assertEquals(expectedOutput, actualOutput);
	}
}
