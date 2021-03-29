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
import java.util.Map;
import javax.xml.transform.TransformerException;

import com.google.common.collect.ImmutableMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.FunctionalTest;
import io.pzstorm.capsid.Utils;

class LaunchRunConfigFunctionalTest extends FunctionalTest {

	private static final Map<LaunchRunConfig, String> RUN_CONFIGS = ImmutableMap.of(
			LaunchRunConfig.RUN_ZOMBOID, "Run_Zomboid.xml",
			LaunchRunConfig.RUN_ZOMBOID_LOCAL, "Run_Zomboid_local.xml",
			LaunchRunConfig.DEBUG_ZOMBOID, "Debug_Zomboid.xml",
			LaunchRunConfig.DEBUG_ZOMBOID_LOCAL, "Debug_Zomboid_local.xml"
	);

	LaunchRunConfigFunctionalTest() {
		super("test");
	}

	@Test
	void shouldWriteToFileLaunchRunConfigurations() throws TransformerException, IOException {

		for (Map.Entry<LaunchRunConfig, String> entry : RUN_CONFIGS.entrySet())
		{
			LaunchRunConfig runConfig = entry.getKey();
			String expectedFilename = entry.getValue();

			File file = runConfig.configure(getProject()).writeToFile();
			Assertions.assertEquals(expectedFilename, file.getName());

			String expected = Utils.readResourceAsTextFromStream(getClass(), expectedFilename);
			String actual = Utils.readTextFromFile(file);

			Assertions.assertEquals(expected, actual);
		}
	}
}
