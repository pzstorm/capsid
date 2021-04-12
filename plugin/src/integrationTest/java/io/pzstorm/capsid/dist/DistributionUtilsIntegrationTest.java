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
package io.pzstorm.capsid.dist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginIntegrationTest;

class DistributionUtilsIntegrationTest extends PluginIntegrationTest {

	@Test
	void shouldGetSourcePathsRelativeToModule() throws IOException {

		Project project = getProject(false);
		project.getPluginManager().apply("java");
		JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
		SourceSet media = java.getSourceSets().create("media");

		media.getJava().setSrcDirs(Collections.singletonList("media/lua"));
		File module = project.file("media");

		String[] filesToCreate = new String[] {
				"media/lua/client/mainClient.lua",
				"media/lua/server/mainServer.lua",
		};
		for (String filePath : filesToCreate)
		{
			File projectFile = project.file(filePath);
			File parentFile = projectFile.getParentFile();
			if (!parentFile.exists()) {
				Assertions.assertTrue(parentFile.mkdirs());
			}
			Assertions.assertTrue(projectFile.createNewFile());
		}
		Map<Path, String> expectedSourceResult = ImmutableMap.of(
				Paths.get("client/mainClient.lua"), "lua",
				Paths.get("server/mainServer.lua"), "lua"
		);
		Assertions.assertEquals(expectedSourceResult,
				DistributionUtils.getPathsRelativeToModule(module, media.getJava()));
	}

	@Test
	void shouldGetResourcePathsRelativeToModule() throws IOException {

		Project project = getProject(false);
		project.getPluginManager().apply("java");
		JavaPluginConvention java = project.getConvention().getPlugin(JavaPluginConvention.class);
		SourceSet media = java.getSourceSets().create("media");

		media.getResources().setSrcDirs(ImmutableList.of("media/models", "media/maps"));
		File module = project.file("media");

		String[] filesToCreate = new String[] {
				"media/models/testModel.obj",
				"media/maps/testMap.map"
		};
		for (String filePath : filesToCreate)
		{
			File projectFile = project.file(filePath);
			File parentFile = projectFile.getParentFile();
			if (!parentFile.exists()) {
				Assertions.assertTrue(parentFile.mkdirs());
			}
			Assertions.assertTrue(projectFile.createNewFile());
		}
		Map<Path, String> expectedResourcesResult = ImmutableMap.of(
				Paths.get("testModel.obj"), "models",
				Paths.get("testMap.map"), "maps"
		);
		Assertions.assertEquals(expectedResourcesResult,
				DistributionUtils.getPathsRelativeToModule(module, media.getResources()));
	}
}
