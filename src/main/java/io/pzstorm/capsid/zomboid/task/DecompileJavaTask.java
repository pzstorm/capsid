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
import java.util.*;

import com.google.common.collect.ImmutableMap;

import org.gradle.api.DefaultTask;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;

import io.pzstorm.capsid.CapsidTask;
import io.pzstorm.capsid.ProjectPropertiesSupplier;
import io.pzstorm.capsid.zomboid.ZomboidTasks;

/**
 * This class decompiles specified classes with FernFlower.
 * Custom compiler parameters can be specified in class constructor.
 */
public class DecompileJavaTask extends DefaultTask implements CapsidTask {

	final ProjectPropertiesSupplier<File> source, destination;
	private final Map<String, Object> parameters;

	DecompileJavaTask(ProjectPropertiesSupplier<File> source,
					  ProjectPropertiesSupplier<File> destination,
					  Map<String, Object> parameters) {

		this.source = source;
		this.destination = destination;
		this.parameters = parameters;
	}

	// default parameters used by IDEA compiler
	DecompileJavaTask(ProjectPropertiesSupplier<File> source, ProjectPropertiesSupplier<File> destination) {
		this(source, destination, ImmutableMap.<String, Object>builder()
				.put(IFernflowerPreferences.HIDE_DEFAULT_CONSTRUCTOR, "0")
				.put(IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, "1")
				.put(IFernflowerPreferences.REMOVE_SYNTHETIC, "1")
				.put(IFernflowerPreferences.REMOVE_BRIDGE, "1")
				.put(IFernflowerPreferences.LITERALS_AS_IS, "1")
				.put(IFernflowerPreferences.NEW_LINE_SEPARATOR, "1")
				.put(IFernflowerPreferences.MAX_PROCESSING_METHOD, "60")
				.build()
		);
	}

	@Override
	public void configure(String group, String description, Project project) {
		CapsidTask.super.configure(group, description, project);

		List<String> args = new ArrayList<>();
		parameters.forEach((k, v) -> args.add(k + '=' + v));

		// decompile to this directory
		File destinationFile = destination.getProjectProperty(project);

		// decompiler will throw error if destination dir doesn't exist
		//noinspection ResultOfMethodCallIgnored
		destinationFile.mkdirs();

		// decompile from these paths
		for (Path sourcePath : getSourcePaths(project)) {
			args.add(sourcePath.toString());
		}
		args.add(destinationFile.toPath().toString());
		setDecompileArguments(project, args);

		dependsOn(project.getTasks().getByName(ZomboidTasks.ZOMBOID_CLASSES.name));
	}

	@TaskAction
	void execute() {
		ConsoleDecompiler.main(getDecompileArguments(getProject()).toArray(new String[0]));
	}

	/**
	 * Returns list of source paths to decompile from.
	 *
	 * @param project {@code Project} used to resolve the project property.
	 */
	List<Path> getSourcePaths(Project project) {
		return new ArrayList<>(Collections.singletonList(source.getProjectProperty(project).toPath()));
	}

	/**
	 * Set arguments to use for decompile task.
	 *
	 * @param project {@code Project} to save the arguments to.
	 */
	void setDecompileArguments(Project project, List<String> args) {
		project.getExtensions().getExtraProperties().set("decompileZomboidArgs", args);
	}

	/**
	 * Returns list of arguments to use for decompile task.
	 *
	 * @param project {@code Project} to load the arguments from.
	 * @throws InvalidUserDataException when decompile arguments are missing.
	 */
	@SuppressWarnings("unchecked")
	List<String> getDecompileArguments(Project project) {

		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
		if (ext.has("decompileZomboidArgs")) {
			return (List<String>) Objects.requireNonNull(ext.get("decompileZomboidArgs"));
		}
		throw new InvalidUserDataException("Missing decompile arguments");
	}
}
