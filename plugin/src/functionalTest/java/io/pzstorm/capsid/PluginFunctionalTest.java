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
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

@Tag("functional")
public abstract class PluginFunctionalTest {

	private static final File PARENT_TEMP_DIR = new File("build/tmp/functionalTest");
	private static final Set<String> TEMP_DIR_NAMES = new HashSet<>();

	private final String projectName;

	private Project project;
	private File projectDir;
	private File gameDir, ideaHome;
	private GradleRunner runner;

	public PluginFunctionalTest() {
		this.projectDir = generateProjectDirectory();
		this.projectName = projectDir.getName();
	}

	protected PluginFunctionalTest(String projectName) {
		this.projectName = projectName;
	}

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

		if (projectDir == null) {
			projectDir = generateProjectDirectory();
		}
		TEMP_DIR_NAMES.add(projectDir.getName());

		// Setup the test build
		Files.createDirectories(projectDir.toPath());
		writeToProjectFile("settings.gradle",
				new String[] { String.format("rootProject.name = '%s'", projectName) }
		);
		writeToProjectFile("build.gradle", new String[] {
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

		gameDir = new File(projectDir, "gameDir");
		Files.createDirectory(gameDir.toPath());

		ideaHome = new File(projectDir, "ideaHome");
		Files.createDirectory(ideaHome.toPath());

		// add project properties
		runner.withArguments(
				"-PgameDir=" + gameDir.toPath().toString(),
				"-PideaHome=" + ideaHome.toPath().toString()
		);
	}

	private Project initializeProject() {

		this.project = ProjectBuilder.builder().withProjectDir(projectDir).build();
		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
		ext.set("gameDir", gameDir.toPath().toString());
		ext.set("ideaHome", ideaHome.toPath().toString());
		return project;
	}

	protected Project getProject() {
		return project != null ? project : initializeProject();
	}

	protected File getProjectDir() {
		return getProject().getProjectDir();
	}

	protected GradleRunner getRunner() {
		return runner;
	}

	private File generateProjectDirectory() {

		// generate a directory name that doesn't exist yet
		File result = getRandomProjectDirectory();
		while (result.exists()) {
			result = getRandomProjectDirectory();
		}
		return result;
	}

	private File getRandomProjectDirectory() {
		return new File(PARENT_TEMP_DIR, "test" + new Random().nextInt(1000));
	}

	protected void writeToProjectFile(String path, String[] lines) throws IOException {
		writeToFile(projectDir.toPath().resolve(path).toFile(), lines);
	}

	protected static void writeToFile(File file, String[] lines) throws IOException {
		try (Writer writer = new FileWriter(file)) {
			writer.write(String.join("\n", lines));
		}
	}
}
