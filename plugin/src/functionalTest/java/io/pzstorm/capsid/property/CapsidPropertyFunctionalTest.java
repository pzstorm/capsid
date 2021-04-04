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
package io.pzstorm.capsid.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.setup.LocalProperties;

class CapsidPropertyFunctionalTest extends PluginFunctionalTest {

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

		Assertions.assertDoesNotThrow(getRunner()::build);
		assertLocalPropertiesNotNull(true);
	}

	@Test
	void shouldLoadLocalPropertiesFromEnvironmentVariables() throws IOException {

		GradleRunner runner = getRunner().withArguments(new ArrayList<>());

		Map<String, String> environment = new HashMap<>();
		environment.put("PZ_DIR_PATH", getGameDirPath().convert().toAbsolutePath().toString());
		environment.put("IDEA_HOME", getIdeaHomePath().convert().toAbsolutePath().toString());
		runner.withEnvironment(environment);

		// runner cannot run in debug mode with environment variables
		runner.withDebug(false);

		Assertions.assertDoesNotThrow(runner::build);
		assertLocalPropertiesNotNull(true);
	}

	private void assertLocalPropertiesNotNull(boolean writeBeforeAssert) throws IOException {

		if (writeBeforeAssert) {
			writeLocalPropertiesToFile();
		}
		// load properties for project before asserting
		LocalProperties localProperties = LocalProperties.get();
		localProperties.load(getProject());

		for (CapsidProperty<?> capsidPropertyEnum : localProperties.getProperties()) {
			Assertions.assertNotNull(capsidPropertyEnum.findProperty(getProject()));
		}
	}

	private void writeLocalPropertiesToFile() throws IOException {

		writeToProjectFile("local.properties", new String[] {
				String.format("gameDir=%s", getGameDirPath().toString()),
				String.format("ideaHome=%s", getIdeaHomePath().toString())
		});
	}
}