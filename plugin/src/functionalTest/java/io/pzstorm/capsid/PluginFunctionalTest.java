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
import java.util.Objects;
import java.util.Random;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import io.pzstorm.capsid.util.UnixPath;
import io.pzstorm.capsid.util.Utils;

@Tag("functional")
public abstract class PluginFunctionalTest {

	private static final File PARENT_TEMP_DIR = new File("build/tmp/functionalTest");

	private final String projectName;

	private Project project;
	private File projectDir;
	private UnixPath gameDir, ideaHome;
	private CapsidGradleRunner runner;

	public PluginFunctionalTest() {
		this.projectDir = generateProjectDirectory();
		this.projectName = projectDir.getName();
	}

	protected PluginFunctionalTest(String projectName) {
		this.projectName = projectName;
	}

	protected static void assertTaskOutcome(BuildResult result, String taskName, TaskOutcome outcome) {

		BuildTask task = Objects.requireNonNull(result.task(':' + taskName));
		Assertions.assertEquals(outcome, task.getOutcome());
	}

	protected static void assertTaskOutcomeSuccess(BuildResult result, String taskName) {
		assertTaskOutcome(result, taskName, TaskOutcome.SUCCESS);
	}

	@BeforeEach
	void createRunner() throws IOException {

		if (projectDir == null) {
			projectDir = new File(PARENT_TEMP_DIR, projectName);
		}
		// make sure the project directory doesn't exist
		if (projectDir.exists()) {
			Utils.deleteDirectory(projectDir);
		}
		// Setup the test build
		Files.createDirectories(projectDir.toPath());
		writeToProjectFile("settings.gradle",
				new String[]{ String.format("rootProject.name = '%s'", projectName) }
		);
		writeToProjectFile("build.gradle", new String[]{
				"plugins {",
				"	id('io.pzstorm.capsid')",
				"}"
		});
		runner = CapsidGradleRunner.create();

		// configure gradle runner
		runner.forwardOutput();
		runner.withPluginClasspath();
		runner.withProjectDir(projectDir);
		runner.withDebug(true);

		gameDir = UnixPath.get(new File(projectDir, "gameDir"));
		Files.createDirectory(gameDir.convert());

		File gameMediaDir = new File(gameDir.convert().toFile(), "media");
		Files.createDirectory(gameMediaDir.toPath());

		for (String dir : new String[]{ "lua", "maps", "models" })
		{
			Path createDir = new File(gameMediaDir, dir).toPath().toAbsolutePath();
			Files.createDirectory(createDir);
			Assertions.assertTrue(createDir.toFile().exists());
		}
		ideaHome = UnixPath.get(new File(projectDir, "ideaHome"));
		Files.createDirectory(ideaHome.convert());

		// add project properties
		//noinspection SpellCheckingInspection
		runner.withArguments(
				"-PgameDir=" + gameDir.toString(),
				"-PideaHome=" + ideaHome.toString()
		);
	}

	private Project initializeProject() {

		this.project = ProjectBuilder.builder().withProjectDir(projectDir).build();
		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
		ext.set("gameDir", gameDir.toString());
		ext.set("ideaHome", ideaHome.toString());
		return project;
	}

	protected Project getProject() {
		return project != null ? project : initializeProject();
	}

	protected File getProjectDir() {
		return getProject().getProjectDir();
	}

	protected CapsidGradleRunner getRunner() {
		return runner;
	}

	protected UnixPath getGameDirPath() {
		return gameDir;
	}

	protected UnixPath getIdeaHomePath() {
		return ideaHome;
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
		try (Writer writer = new FileWriter(projectDir.toPath().resolve(path).toFile())) {
			writer.write(String.join("\n", lines));
		}
	}
}
