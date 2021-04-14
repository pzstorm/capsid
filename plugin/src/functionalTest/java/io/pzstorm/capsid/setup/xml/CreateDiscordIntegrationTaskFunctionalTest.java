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
import java.util.ArrayList;
import java.util.List;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.setup.SetupTasks;
import io.pzstorm.capsid.util.Utils;

class CreateDiscordIntegrationTaskFunctionalTest extends PluginFunctionalTest {

	CreateDiscordIntegrationTaskFunctionalTest() {
		super("testDiscordIntegration");
	}

	@Test
	void shouldCreateDiscordIntegrationConfigurationFile() throws IOException {

		GradleRunner runner = getRunner();
		List<String> arguments = new ArrayList<>(runner.getArguments());
		arguments.add(SetupTasks.CREATE_DISCORD_INTEGRATION.name);
		arguments.add("-Pmod.description=Testing Discord integration.");

		BuildResult result = getRunner().withArguments(arguments).build();
		assertTaskOutcomeSuccess(result, SetupTasks.CREATE_DISCORD_INTEGRATION.name);

		String expected = Utils.readResourceAsTextFromStream(getClass(), "discord.xml");
		String actual = Utils.readTextFromFile(new File(getProjectDir(), ".idea/discord.xml"));
		Assertions.assertEquals(expected, actual);
	}
}
