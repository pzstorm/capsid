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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

@Tag("functional")
public abstract class FunctionalTest {

	private static final File PARENT_TEMP_DIR = new File("build/tmp/functionalTest");
	private static final Set<String> TEMP_DIR_NAMES = new HashSet<>();

	private Project project;
	private GradleRunner runner;

	@BeforeAll
	static void cleanTemporaryDirectories() throws IOException {

		if (PARENT_TEMP_DIR.exists())
		{
			Set<Path> dirsToClean = Files.list(PARENT_TEMP_DIR.toPath()).filter(p ->
					p.toFile().isDirectory() && !TEMP_DIR_NAMES.contains(p.getFileName().toString()))
					.collect(Collectors.toSet());

			for (Path path : dirsToClean)
			{
				MoreFiles.deleteRecursively(path, RecursiveDeleteOption.ALLOW_INSECURE);
				Assertions.assertFalse(path.toFile().exists());
			}
		}
	}

	@BeforeEach
	void createRunner() throws IOException {

		String dirName = "test" + new Random().nextInt(1000);
		File projectDir = new File(PARENT_TEMP_DIR, dirName);
		TEMP_DIR_NAMES.add(dirName);

		// some functional tests will need Project instance
		project = ProjectBuilder.builder().withProjectDir(projectDir).build();

		// Setup the test build
		Files.createDirectories(projectDir.toPath());
		writeToFile(new File(projectDir, "settings.gradle"));
		writeToFile(new File(projectDir, "build.gradle"), new String[] {
				"plugins {",
				"	id('io.pzstorm.capsid')",
				"}"
		});
		runner = GradleRunner.create();

		// configure gradle runner
		runner.forwardOutput();
		runner.withPluginClasspath();
		runner.withProjectDir(projectDir);
		runner.withDebug(true);

		// add project properties
		runner.withArguments(
				"-PgameDir=C:/ProjectZomboid/",
				"-PideaHome=C:/IntelliJ IDEA/"
		);
	}

	protected Project getProject() {
		return project;
	}

	protected File getProjectDir() {
		return project.getProjectDir();
	}

	protected GradleRunner getRunner() {
		return runner;
	}

	protected static void writeToFile(File file, String[] lines) throws IOException {
		try (Writer writer = new FileWriter(file)) {
			writer.write(String.join("\n", lines));
		}
	}

	protected static void writeToFile(File file, String string) throws IOException {
		try (Writer writer = new FileWriter(file)) {
			writer.write(string);
		}
	}

	protected static void writeToFile(File file) throws IOException {
		try (Writer writer = new FileWriter(file)) {
			writer.write("");
		}
	}
}
