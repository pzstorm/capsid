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

import java.io.IOException;
import java.util.Objects;

import org.gradle.api.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginIntegrationTest;
import io.pzstorm.capsid.property.CapsidProperty;
import io.pzstorm.capsid.util.Utils;

class LocalPropertiesIntegrationTest extends PluginIntegrationTest {

	@Test
	void shouldReturnFalseWhenLoadingNonExistingLocalProperties() {

		Project project = getProject(false);
		LocalProperties localProperties = LocalProperties.get();

		Assertions.assertTrue(localProperties.getFile(project).delete());
		Assertions.assertFalse(localProperties.load(project));
	}

	@Test
	void shouldWriteLocalPropertiesToFile() throws IOException {

		Project project = getProject(false);
		LocalProperties localProperties = LocalProperties.get();

		writeToProjectFile("local.properties", new String[]{
				String.format("gameDir=%s", getGameDirPath()),
				String.format("ideaHome=%s", getIdeaHomePath())
		});
		// load properties for project before asserting
		localProperties.load(project);

		// write properties to file
		localProperties.writeToFile(project);

		StringBuilder sb = new StringBuilder();
		String[] expectedFileComments = new String[]{
				"#This file contains local properties used to configure project build",
				"#Note: paths need to be Unix-style where segments " +
						"need to be separated with forward-slashes (/)",
				"#this is for compatibility and stability purposes as backslashes don't play well."
		};
		sb.append(String.join("\n", expectedFileComments));
		for (CapsidProperty<?> property : localProperties.getProperties())
		{
			String sProperty = Objects.requireNonNull(property.findProperty(project)).toString();

			sb.append("\n\n").append("#").append(property.comment).append('\n');
			sb.append(property.name).append('=').append(sProperty.replace('\\', '/'));
		}
		String expected = sb.toString();
		localProperties.writeToFile(project);
		String actual = Utils.readTextFromFile(localProperties.getFile(project));
		Assertions.assertEquals(expected, actual);
	}
}
