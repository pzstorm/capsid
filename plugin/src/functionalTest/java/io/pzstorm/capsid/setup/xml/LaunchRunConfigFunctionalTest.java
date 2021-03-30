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
package io.pzstorm.capsid.setup.xml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.Utils;

class LaunchRunConfigFunctionalTest extends PluginFunctionalTest {

	private static final Map<LaunchRunConfig, String> RUN_CONFIGS = ImmutableMap.of(
			LaunchRunConfig.RUN_ZOMBOID, "Run_Zomboid.xml",
			LaunchRunConfig.RUN_ZOMBOID_LOCAL, "Run_Zomboid_local.xml",
			LaunchRunConfig.DEBUG_ZOMBOID, "Debug_Zomboid.xml",
			LaunchRunConfig.DEBUG_ZOMBOID_LOCAL, "Debug_Zomboid_local.xml"
	);

	LaunchRunConfigFunctionalTest() {
		super("testLaunchRunConfigs");
	}

	@Test
	void shouldWriteToFileLaunchRunConfigurationsFromTask() throws IOException {

		List<String> arguments = ImmutableList.of(
				String.format("-PgameDir=%s", getGameDirPath().toString()),
				String.format("-PideaHome=%s", getIdeaHomePath().toString()),
				"createLaunchRunConfigs"
		);
		BuildResult result = getRunner().withArguments(arguments).build();
		BuildTask task = Objects.requireNonNull(result.task(":createLaunchRunConfigs"));
		Assertions.assertEquals(TaskOutcome.SUCCESS, task.getOutcome());

		Path runConfigurations = getProjectDir().toPath().resolve(".idea/runConfigurations");
		for (Map.Entry<LaunchRunConfig, String> entry : RUN_CONFIGS.entrySet())
		{
			String filename = entry.getValue();
			File runConfig = runConfigurations.resolve(filename).toFile();
			Assertions.assertTrue(runConfig.exists());

			String expected = Utils.readResourceAsTextFromStream(getClass(), filename);
			String actual = Utils.readTextFromFile(runConfig);

			Assertions.assertEquals(expected, actual);
		}
	}
}
