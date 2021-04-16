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
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.MoreFiles;

import io.pzstorm.capsid.CapsidPlugin;
import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.util.Utils;
import io.pzstorm.capsid.zomboid.ZomboidTasks;

class ZomboidJarTaskFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldAssembleJarArchiveContainingGameClasses() throws IOException {

		File source = CapsidPlugin.getGameDirProperty(getProject());
		Assertions.assertTrue(ProjectProperty.ZOMBOID_CLASSES_DIR.get(getProject()).mkdirs());

		File destination = new File(getProject().getProjectDir(), "lib");
		Assertions.assertTrue(destination.mkdirs());

		Set<String> filesToInclude = ImmutableSet.of(
				"class1.class", "class2.class", "class3.class"
		);
		File dummyClass = Utils.getFileFromResources("dummy.class");
		for (String include : filesToInclude) {
			com.google.common.io.Files.copy(dummyClass, new File(source, include));
		}
		Map<String, File> filesToExclude = ImmutableMap.of(
				"textFile.txt", Utils.getFileFromResources("dummy.txt"),
				"imageFile.png", Utils.getFileFromResources("dummy.png")
		);
		for (Map.Entry<String, File> entry : filesToExclude.entrySet()) {
			com.google.common.io.Files.copy(entry.getValue(), new File(source, entry.getKey()));
		}
		BuildResult result = getRunner().withArguments(ZomboidTasks.ZOMBOID_JAR.name).build();
		assertTaskOutcomeSuccess(result, ZomboidTasks.ZOMBOID_JAR.name);

		try (Stream<Path> stream = java.nio.file.Files.walk(destination.toPath()))
		{
			Utils.unzipArchive(stream.filter(f -> MoreFiles.getFileExtension(f).equals("jar"))
					.findAny().orElseThrow(RuntimeException::new).toFile(), destination);
		}
		for (String include : filesToInclude) {
			Assertions.assertTrue(new File(destination, include).exists());
		}
		for (Map.Entry<String, File> entry : filesToExclude.entrySet()) {
			Assertions.assertFalse(new File(destination, entry.getKey()).exists());
		}
	}
}
