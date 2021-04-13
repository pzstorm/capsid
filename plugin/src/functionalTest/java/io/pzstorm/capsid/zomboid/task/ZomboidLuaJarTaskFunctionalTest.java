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
package io.pzstorm.capsid.zomboid.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

import org.gradle.api.Project;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.util.Utils;

class ZomboidLuaJarTaskFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldAssembleJarWithCompiledLuaClasses() throws IOException {

		Project project = getProject();
		File zDocLuaDir = ProjectProperty.ZDOC_LUA_DIR.get(project);
		File destination = project.file("lib");

		String[] expectedFiles = new String[]{
				"luaFile1.lua", "luaFile2.lua", "luaFile3.lua"
		};
		// create directory structure before creating files
		Assertions.assertTrue(zDocLuaDir.mkdirs());

		// create expected files in lua directory
		for (String expectedFile : expectedFiles)
		{
			Assertions.assertTrue(new File(zDocLuaDir, expectedFile).createNewFile());
		}
		// create directory structure before walking
		Assertions.assertTrue(destination.mkdir());

		// assert no files present in destination
		Assertions.assertEquals(0, destination.listFiles().length);

		GradleRunner runner = getRunner();
		List<String> arguments = new ArrayList<>(runner.getArguments());
		arguments.add("zomboidLuaJar");

		BuildResult result = runner.withArguments(arguments).build();
		assertTaskOutcomeSuccess(result, "zomboidLuaJar");

		// confirm archive was created
		File archive = new File(destination, "zdoc-lua.jar");
		Assertions.assertTrue(archive.exists());

		// assert only jar file present in destination directory
		Assertions.assertEquals(1, destination.listFiles().length);

		Utils.unzipArchive(archive, destination);

		Path manifest = new File(destination, "META-INF").toPath();
		MoreFiles.deleteRecursively(manifest, RecursiveDeleteOption.ALLOW_INSECURE);
		Assertions.assertTrue(archive.delete());

		// assert only expected files are in directory
		Assertions.assertEquals(expectedFiles.length, destination.listFiles().length);

		for (String expectedFile : expectedFiles)
		{
			Assertions.assertTrue(new File(destination, expectedFile).exists());
		}
	}
}
