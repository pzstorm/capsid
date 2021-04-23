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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.pzstorm.capsid.zomboid.ZomboidTasks;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;

import com.google.common.base.Splitter;

import io.pzstorm.capsid.ProjectProperty;

/**
 * Decompile game classes with FernFlower using default IDEA settings.
 * Default task behaviour is to decompile all class files found in game root directory.
 * <p>
 * This can be changed by defining specific files to decompile with project property 'decompileFiles'.
 * Each specified file path has to be a package path relative to zomboid classes directory.
 * When specifying multiple file paths remember to separate them with comma delimiter.
 * </p>
 * <ul><li>Example:</li>
 * <pre>
 * gradle decompileZomboid -PdecompileFiles=zombie/FileGuidPair.class,zombie/GameTime.class
 * </pre></ul>
 * </p>
 */
public class DecompileZomboidTask extends DecompileJavaTask {

	@Inject
	public DecompileZomboidTask() {
		super(ProjectProperty.ZOMBOID_CLASSES_DIR.getSupplier(), ProjectProperty.ZOMBOID_SOURCES_DIR.getSupplier());
	}

	@Override
	public void configure(String group, String description, Project project) {
		super.configure(group, description, project);

		finalizedBy(project.getTasks().getByName(ZomboidTasks.ZOMBOID_SOURCES_JAR.name));
	}

	@Override
	public List<Path> getSourcePaths(Project project) {

		ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
		if (ext.has("decompileFiles"))
		{
			List<Path> sourcePaths = new ArrayList<>();
			Path sourcePath = getSourcePathFromObject(source.getProjectProperty(project));

			String sDecompileFiles = (String) Objects.requireNonNull(ext.get("decompileFiles"));
			List<String> decompileFiles = Splitter.on(',').splitToList(sDecompileFiles);
			if (!decompileFiles.isEmpty())
			{
				decompileFiles.forEach(f -> sourcePaths.add(sourcePath.resolve(f)));
				return sourcePaths;
			}
		}
		return super.getSourcePaths(project);
	}
}
