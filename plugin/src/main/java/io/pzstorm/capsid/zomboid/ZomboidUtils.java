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
package io.pzstorm.capsid.zomboid;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.api.GradleException;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;

import io.pzstorm.capsid.Configurations;
import io.pzstorm.capsid.ProjectProperty;
import io.pzstorm.capsid.util.SemanticVersion;

/**
 * This class contains helper methods used by {@link ZomboidTasks}.
 */
public class ZomboidUtils {

	/**
	 * Returns {@code File} that stores zomboid version data.
	 *
	 * @param project {@code Project} requesting the file instance.
	 *
	 * @throws IOException If an I/O error occurred while creating file.
	 */
	public static File getZomboidVersionFile(Project project) throws IOException {

		File versionFile = ProjectProperty.ZDOC_VERSION_FILE.get(project);
		if (!versionFile.exists() && !versionFile.createNewFile()) {
			throw new IOException("Unable to create 'zdoc.version' file");
		}
		return versionFile;
	}

	/**
	 * Returns the ZomboidDoc dependency version used by given project.
	 *
	 * @throws GradleException if unable to find dependency or dependency has unexpected name.
	 * @throws InvalidUserDataException if constructed semantic version is malformed.
	 */
	public static SemanticVersion getZomboidDocVersion(Project project) {

		// find ZomboidDoc dependency file from configuration
		File dependency = Configurations.ZOMBOID_DOC.resolve(project).getFiles().stream()
				.filter(f -> f.getName().startsWith("pz-zdoc")).findFirst().orElseThrow(
						() -> new GradleException("Unable to find ZomboidDoc dependency"));

		// get and validate dependency name
		String dependencyName = dependency.getName();

		String pattern = "pz-zdoc-(\\d+\\.\\d+\\.\\d+(-.*)?)\\.jar";
		Matcher matcher = Pattern.compile(pattern).matcher(dependencyName);
		if (!matcher.find()) {
			throw new GradleException("Unexpected ZomboidDoc dependency name: " + dependencyName);
		}
		return new SemanticVersion(matcher.group(1));
	}
}
