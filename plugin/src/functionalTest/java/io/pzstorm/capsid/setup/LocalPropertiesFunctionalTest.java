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
package io.pzstorm.capsid.setup;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.util.Utils;
import io.pzstorm.capsid.property.LocalProperty;

class LocalPropertiesFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldLoadLocalPropertiesFromFile() throws IOException {

		writeLocalPropertiesToFile();
		Assertions.assertDoesNotThrow(() -> getRunner().withArguments(new ArrayList<>()).build());
		assertLocalPropertiesNotNull(false);
	}

	@Test
	void shouldLoadLocalPropertiesFromProjectProperties() throws IOException {

		Assertions.assertDoesNotThrow(() -> getRunner().build());
		assertLocalPropertiesNotNull(true);
	}

	@Test
	void shouldLoadLocalPropertiesFromSystemProperties() throws IOException {

		GradleRunner runner = getRunner();
		runner.withArguments(
				String.format("-DgameDir=%s", getGameDirPath().toString()),
				String.format("-DideaHome=%s", getIdeaHomePath().toString())
		);
		Assertions.assertDoesNotThrow(runner::build);
		assertLocalPropertiesNotNull(true);
	}

	@Test
	void shouldLoadLocalPropertiesFromEnvironmentVariables() throws IOException {

		GradleRunner runner = getRunner().withArguments(new ArrayList<>(Collections.singletonList("--stacktrace")));

		Map<String, String> arguments = new HashMap<>();
		arguments.put("PZ_DIR_PATH", getGameDirPath().toString());
		arguments.put("IDEA_HOME", getIdeaHomePath().toString());
		runner.withEnvironment(arguments);

		// runner cannot run in debug mode with environment variables
		runner.withDebug(false);

		Assertions.assertDoesNotThrow(runner::build);
		assertLocalPropertiesNotNull(true);
	}

	@Test
	void shouldWriteLocalPropertiesToFile() throws IOException {

		writeToFile(new File(getProjectDir(), "local.properties"), new String[] {
				String.format("gameDir=%s", getGameDirPath().toString()),
				String.format("ideaHome=%s", getIdeaHomePath().toString())
		});
		// load properties for project before asserting
		LocalProperties.load(getProject());

		// write properties to file
		LocalProperties.writeToFile(getProject());

		StringBuilder sb = new StringBuilder();
		String[] expectedFileComments = new String[] {
				"#This file contains local properties used to configure project build",
				"#Note: paths need to be Unix-style where segments need to be separated with forward-slashes (/)",
				"#this is for compatibility and stability purposes as backslashes don't play well."
		};
		sb.append(String.join("\n", expectedFileComments));
		for (LocalProperty<?> property : LocalProperties.get())
		{
			String sProperty = Objects.requireNonNull(property.findProperty(getProject())).toString();

			sb.append("\n\n").append("#").append(property.comment).append('\n');
			sb.append(property.name).append('=').append(sProperty.replace('\\', '/'));
		}
		String expected = sb.toString();
		LocalProperties.writeToFile(getProject());
		String actual = Utils.readTextFromFile(LocalProperties.getFile(getProject()));
		Assertions.assertEquals(expected, actual);
	}

	private void assertLocalPropertiesNotNull(boolean writeBeforeAssert) throws IOException {

		if (writeBeforeAssert) {
			writeLocalPropertiesToFile();
		}
		// load properties for project before asserting
		LocalProperties.load(getProject());

		for (LocalProperty<?> localPropertyEnum : LocalProperties.get()) {
			Assertions.assertNotNull(localPropertyEnum.findProperty(getProject()));
		}
	}

	private void writeLocalPropertiesToFile() throws IOException {

		writeToFile(new File(getProjectDir(), "local.properties"), new String[] {
				String.format("gameDir=%s", getGameDirPath().toString()),
				String.format("ideaHome=%s", getIdeaHomePath().toString())
		});
	}
}
