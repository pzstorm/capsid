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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.io.Files;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.FunctionalTest;

class LocalPropertiesFunctionalTest extends FunctionalTest {

	@Test
	void shouldLoadLocalPropertiesFromFile() throws IOException {

		writeLocalPropertiesToFile();
		Assertions.assertDoesNotThrow(() -> getRunner().withArguments(new ArrayList<>()).build());
		assertLocalPropertiesNotNull(false);
	}

	@Test
	void shouldLoadLocalPropertiesFromSystemProperties() throws IOException {

		GradleRunner runner = getRunner();
		runner.withArguments(
				"-DgameDir=C:/ProjectZomboid/",
				"-DideaHome=C:/IntelliJ IDEA/"
		);
		Assertions.assertDoesNotThrow(runner::build);
		assertLocalPropertiesNotNull(true);
	}

	@Test
	void shouldLoadLocalPropertiesFromEnvironmentVariables() throws IOException {

		GradleRunner runner = getRunner().withArguments(new ArrayList<>());

		Map<String, String> arguments = new HashMap<>();
		arguments.put("PZ_DIR_PATH", "C:/ProjectZomboid/");
		arguments.put("IDEA_HOME", "C:/IntelliJ IDEA/");
		runner.withEnvironment(arguments);

		// runner cannot run in debug mode with environment variables
		runner.withDebug(false);

		Assertions.assertDoesNotThrow(runner::build);
		assertLocalPropertiesNotNull(true);
	}

	@Test
	void shouldWriteLocalPropertiesToFile() throws IOException {

		writeToFile(new File(getProjectDir(), "local.properties"), new String[] {
			"gameDir=C:/ProjectZomboid", "ideaHome=C:/IntelliJ IDEA"
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
		for (LocalProperties property : LocalProperties.values())
		{
			String sProperty = Objects.requireNonNull(property.data.getProperty()).toString();

			sb.append("\n\n").append("#").append(property.data.comment).append('\n');
			sb.append(property.data.name).append('=').append(sProperty.replace('\\', '/'));
		}
		String expected = sb.toString();
		LocalProperties.writeToFile(getProject());
		String actual = String.join("\n", Files.readLines(
				LocalProperties.getFile(getProject()), StandardCharsets.UTF_8
		));
		Assertions.assertEquals(expected, actual);
	}

	private void assertLocalPropertiesNotNull(boolean writeBeforeAssert) throws IOException {

		if (writeBeforeAssert) {
			writeLocalPropertiesToFile();
		}
		// load properties for project before asserting
		LocalProperties.load(getProject());

		for (LocalProperties localPropertyEnum : LocalProperties.values()) {
			Assertions.assertNotNull(localPropertyEnum.data.getProperty());
		}
	}

	private void writeLocalPropertiesToFile() throws IOException {

		writeToFile(new File(getProjectDir(), "local.properties"), new String[] {
				"gameDir=C:/ProjectZomboid/", "ideaHome=C:/IntelliJ IDEA/"
		});
	}
}
