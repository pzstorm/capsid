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
import java.util.Map;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.setup.SetupTasks;
import io.pzstorm.capsid.util.Utils;

class CreateRunConfigurationsTaskFunctionalTest extends PluginFunctionalTest {

	private static final ImmutableMap<XMLDocument, String> RUN_CONFIGS =
			ImmutableMap.<XMLDocument, String>builder()
					.put(LaunchRunConfig.RUN_ZOMBOID, "Run_Zomboid.xml")
					.put(LaunchRunConfig.RUN_ZOMBOID_LOCAL, "Run_Zomboid_local.xml")
					.put(LaunchRunConfig.DEBUG_ZOMBOID, "Debug_Zomboid.xml")
					.put(LaunchRunConfig.DEBUG_ZOMBOID_LOCAL, "Debug_Zomboid_local.xml")
					.put(GradleRunConfig.INITIALIZE_MOD, "initializeMod.xml")
					.put(GradleRunConfig.SETUP_WORKSPACE, "setupWorkspace.xml")
					.build();

	CreateRunConfigurationsTaskFunctionalTest() {
		super("testLaunchRunConfigs");
	}

	@Test
	void shouldWriteToFileLaunchRunConfigurationsFromTask() throws IOException {

		BuildResult result = getRunner().withArguments(SetupTasks.CREATE_RUN_CONFIGS.name).build();
		assertTaskOutcomeSuccess(result, SetupTasks.CREATE_RUN_CONFIGS.name);

		File runConfigurations = new File(getProjectDir(), ".idea/runConfigurations");
		for (Map.Entry<XMLDocument, String> entry : RUN_CONFIGS.entrySet())
		{
			String filename = entry.getValue();
			File runConfig = new File(runConfigurations, filename);
			Assertions.assertTrue(runConfig.exists());

			String expected = Utils.readResourceAsTextFromStream(getClass(), filename);
			Assertions.assertEquals(expected, Utils.readTextFromFile(runConfig));
		}
	}
}
