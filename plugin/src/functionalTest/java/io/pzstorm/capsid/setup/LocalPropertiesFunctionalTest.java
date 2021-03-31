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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.util.Utils;
import io.pzstorm.capsid.property.CapsidProperty;

class LocalPropertiesFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldWriteLocalPropertiesToFile() throws IOException {

		LocalProperties localProperties = LocalProperties.get();
		writeToFile(new File(getProjectDir(), "local.properties"), new String[] {
				String.format("gameDir=%s", getGameDirPath().toString()),
				String.format("ideaHome=%s", getIdeaHomePath().toString())
		});
		// load properties for project before asserting
		localProperties.load(getProject());

		// write properties to file
		localProperties.writeToFile(getProject());

		StringBuilder sb = new StringBuilder();
		String[] expectedFileComments = new String[] {
				"#This file contains local properties used to configure project build",
				"#Note: paths need to be Unix-style where segments need to be separated with forward-slashes (/)",
				"#this is for compatibility and stability purposes as backslashes don't play well."
		};
		sb.append(String.join("\n", expectedFileComments));
		for (CapsidProperty<?> property : localProperties.getProperties())
		{
			String sProperty = Objects.requireNonNull(property.findProperty(getProject())).toString();

			sb.append("\n\n").append("#").append(property.comment).append('\n');
			sb.append(property.name).append('=').append(sProperty.replace('\\', '/'));
		}
		String expected = sb.toString();
		localProperties.writeToFile(getProject());
		String actual = Utils.readTextFromFile(localProperties.getFile(getProject()));
		Assertions.assertEquals(expected, actual);
	}
}
