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
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.setup.SetupTasks;
import io.pzstorm.capsid.util.Utils;

class CreateSearchScopesFunctionalTest extends PluginFunctionalTest {

	private static final ImmutableMap<ModSearchScope, String> SEARCH_SCOPES = ImmutableMap.of(
			ModSearchScope.MOD_LUA, "mod_lua.xml",
			ModSearchScope.MOD_MEDIA, "mod_media.xml",
			ModSearchScope.PZ_JAVA, "pz_java.xml",
			ModSearchScope.PZ_LUA, "pz_lua.xml"
	);
	private static final ImmutableMap<ModSearchScope, String[]> SEARCH_SCOPES_SUBPROJECT = ImmutableMap.of(
			ModSearchScope.MOD_LUA, new String[]{ "mod_lua.xml", "mod_lua_subproject.xml" },
			ModSearchScope.MOD_MEDIA, new String[]{ "mod_media.xml", "mod_media_subproject.xml" }
	);

	CreateSearchScopesFunctionalTest() {
		super("modSearchScopes");
	}

	@Test
	void shouldWriteToFileModSearchScopes() throws IOException {

		BuildResult result = getRunner().withArguments(SetupTasks.CREATE_SEARCH_SCOPES.name).build();
		assertTaskOutcomeSuccess(result, SetupTasks.CREATE_SEARCH_SCOPES.name);

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

	@Test
	void shouldWriteToRootProjectModSearchScopes() throws IOException {

		GradleRunner runner = getRunner();
		File projectDir = runner.getProjectDir();

		File subProjectDir = new File(projectDir, "subproject");
		Assertions.assertTrue(subProjectDir.mkdir());

		writeToProjectFile("settings.gradle", new String[]{
				"include 'subproject'"
		});
		File copyDestination = new File(subProjectDir, "build.gradle");
		Files.copy(new File(projectDir, "build.gradle"), copyDestination);

		BuildResult result = runner.withArguments(SetupTasks.CREATE_SEARCH_SCOPES.name).build();
		assertTaskOutcomeSuccess(result, SetupTasks.CREATE_SEARCH_SCOPES.name);

		Assertions.assertFalse(new File(subProjectDir, ".idea/scopes").exists());
		Assertions.assertTrue(new File(projectDir, ".idea/scopes").exists());

		File searchScopes = new File(projectDir, ".idea/scopes");
		for (Map.Entry<ModSearchScope, String[]> entry : SEARCH_SCOPES_SUBPROJECT.entrySet())
		{
			File searchScope = new File(searchScopes, entry.getValue()[1]);
			Assertions.assertTrue(searchScope.exists());

			String expected = Utils.readResourceAsTextFromStream(getClass(), searchScope.getName());
			String actual = Utils.readTextFromFile(searchScope);

			Assertions.assertEquals(expected, actual);
		}
	}
}
