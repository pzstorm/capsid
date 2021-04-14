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
import java.util.Map;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.setup.SetupTasks;
import io.pzstorm.capsid.util.Utils;

class CreateModSearchScopesFunctionalTest extends PluginFunctionalTest {

	private static final ImmutableMap<ModSearchScope, String> SEARCH_SCOPES = ImmutableMap.of(
			ModSearchScope.MOD_LUA, "mod_lua.xml",
			ModSearchScope.MOD_MEDIA, "mod_media.xml"
	);

	CreateModSearchScopesFunctionalTest() {
		super("modSearchScopes");
	}

	@Test
	void shouldWriteToFileModSearchScopes() throws IOException {

		BuildResult result = getRunner().withArguments(SetupTasks.CREATE_MOD_SEARCH_SCOPES.name).build();
		assertTaskOutcomeSuccess(result, SetupTasks.CREATE_MOD_SEARCH_SCOPES.name);

		Path searchScopes = getProjectDir().toPath().resolve(".idea/scopes/");
		for (Map.Entry<ModSearchScope, String> entry : SEARCH_SCOPES.entrySet())
		{
			String filename = entry.getValue();
			File runConfig = searchScopes.resolve(filename).toFile();
			Assertions.assertTrue(runConfig.exists());

			String expected = Utils.readResourceAsTextFromStream(getClass(), filename);
			String actual = Utils.readTextFromFile(runConfig);

			Assertions.assertEquals(expected, actual);
		}
	}
}
