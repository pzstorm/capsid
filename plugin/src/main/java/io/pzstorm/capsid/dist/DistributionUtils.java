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
package io.pzstorm.capsid.dist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.GradleException;
import org.gradle.api.file.SourceDirectorySet;

public class DistributionUtils {

	/**
	 * Returns a {@code Map} that contains paths relative to given module. Paths relate to existing
	 * files from given {@code SourceDirectorySet}. The resulting map keys represent the source
	 * directory, while the values represent paths relative to that source directory.
	 *
	 * @param module {@code File} representing module root directory.
	 * @param srcDirSet {@code SourceDirectorySet} to extract paths from.
	 * @return {@code Map} that contains paths relative to given module.
	 */
	public static Map<Path, String> getPathsRelativeToModule(File module, SourceDirectorySet srcDirSet) {

		Map<Path, String> result = new HashMap<>();
		File moduleDir = module.getAbsoluteFile();
		if (!moduleDir.exists())
		{
			throw new GradleException("Unable to find module directory for path '" + moduleDir.getPath() + '\'');
		}
		// collect all existing source directories for given directory set
		Set<File> srcDirs = srcDirSet.getSrcDirs().stream()
				.filter(File::exists).collect(Collectors.toSet());

		for (File srcDir : srcDirs)
		{
			// existing file paths found in source directory
			Set<Path> paths;
			try
			{
				// recursively collect all file paths in source directory
				paths = Files.walk(srcDir.toPath()).filter(Files::isRegularFile).collect(Collectors.toSet());
			}
			catch (IOException e)
			{
				throw new GradleException("I/O error occurred while walking file tree", e);
			}
			Path srcDirPath = srcDir.toPath();
			for (Path path : paths)
			{
				// path relative to the source directory
				Path relativeToSrcDir = srcDirPath.relativize(path);

				// path to source root directory
				Path srdRootDirectory = moduleDir.toPath().relativize(srcDirPath);
				result.put(relativeToSrcDir, srdRootDirectory.toString());
			}
		}
		return result;
	}
}
