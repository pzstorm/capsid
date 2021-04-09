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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Splitter;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.JavaExec;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainService;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.setup.LocalProperties;
import io.pzstorm.capsid.util.UnixPath;
import io.pzstorm.capsid.zomboid.ZomboidTasks;

/**
 * Decompile game classes with FernFlower using default IDEA settings.
 * Default task behaviour is to decompile all class files found in game root directory.
 * <p>
 * This can be changed by defining specific files to decompile with project property 'decompileFiles'.
 * Each specified file path has to be a package path relative to destination root directory.
 * When specifying multiple file paths remember to separate them with comma delimiter.
 * </p>
 * <ul><li>example:</li>
 * <pre>
 * gradle decompileZomboid -PdecompileFiles=zombie/FileGuidPair.class,zombie/GameTime.class
 * </pre></ul>
 * </p>
 */
public class DecompileZomboidTask extends JavaExec implements CapsidTask {

	@Override
	public void configure(String group, String description, Project project) {

		setGroup(group);
		setDescription(description);

		UnixPath property = LocalProperties.IDEA_HOME.findProperty(project);
		if (property != null)
		{
			File zomboidClassesDir = ProjectProperty.ZOMBOID_CLASSES_DIR.get(project);
			onlyIf(t -> zomboidClassesDir.exists() && zomboidClassesDir.listFiles().length > 0);

			ExtensionContainer extensions = project.getExtensions();
			ExtraPropertiesExtension ext = extensions.getExtraProperties();

			// set task to run with Java 11
			JavaToolchainService toolchain = extensions.getByType(JavaToolchainService.class);
			getJavaLauncher().set(toolchain.launcherFor(
					t -> t.getLanguageVersion().set(JavaLanguageVersion.of(11)))
			);
			File ideaHome = property.convert().toAbsolutePath().toFile();
			classpath(new File(ideaHome, "plugins/java-decompiler/lib/java-decompiler.jar"));
			setMain("org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler");

			// default parameters used by IDEA compiler
			List<String> parameters = new ArrayList<>(Arrays.asList(
					"-hdc=0", "-dgs=1", "-rsy=1", "-rbr=1", "-lit=1", "-nls=1", "-mpm=60"
			));
			File zomboidSourcesDir = ProjectProperty.ZOMBOID_SOURCES_DIR.get(project);
			// decompiler will throw error if destination dir doesn't exist
			//noinspection ResultOfMethodCallIgnored
			zomboidSourcesDir.mkdirs();

			// decompile from this directory
			Path from = zomboidClassesDir.toPath();
			if (ext.has("decompileFiles"))
			{
				String decompileFiles = (String) Objects.requireNonNull(ext.get("decompileFiles"));
				Splitter.on(',').split(decompileFiles).forEach(f -> parameters.add(from.resolve(f).toString()));
			}
			else parameters.add(from.toString());

			// decompile to this directory
			parameters.add(zomboidClassesDir.toPath().toString());

			setArgs(parameters);
			dependsOn(project.getTasks().getByName(ZomboidTasks.ZOMBOID_CLASSES.name));
		}
	}
}
