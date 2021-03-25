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
package io.pzstorm.capsid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class PluginUnitTest {

	private static final File PARENT_TEMP_DIR = new File("build/tmp/unitTest");
	private static final Set<String> TEMP_DIR_NAMES = new HashSet<>();

	private Project project;
	private Plugin<Project> plugin;

	@BeforeAll
	static void cleanTemporaryDirectories() throws IOException {

		if (PARENT_TEMP_DIR.exists())
		{
			Set<Path> dirsToClean = Files.list(PARENT_TEMP_DIR.toPath()).filter(p ->
					p.toFile().isDirectory() && !TEMP_DIR_NAMES.contains(p.getFileName().toString()))
					.collect(Collectors.toSet());

			for (Path path : dirsToClean)
			{
				File dir = path.toFile();
				FileUtils.deleteDirectory(dir);
				Assertions.assertFalse(dir.exists());
			}
		}
	}

	@BeforeEach
	@SuppressWarnings("unchecked")
	void createProjectAndApplyPlugin() {

		String dirName = "test" + new Random().nextInt(1000);
		File tempDir = new File(PARENT_TEMP_DIR, dirName);
		TEMP_DIR_NAMES.add(dirName);

		project = ProjectBuilder.builder().withProjectDir(tempDir).build();
		plugin = project.getPlugins().apply("io.pzstorm.capsid");
	}

	protected Project getProject() {
		return project;
	}

	protected Plugin<Project> getPlugin() {
		return plugin;
	}
}
