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

import java.util.*;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;

import io.pzstorm.capsid.dist.GenerateChangelogOptions;
import io.pzstorm.capsid.dist.task.GenerateChangelogTask;

/**
 * Extension used to configure how {@link CapsidPlugin} works.
 */
@SuppressWarnings({ "WeakerAccess", "CanBeFinal" })
public class CapsidPluginExtension {

	private final Set<String> excludedResourceDirs = new HashSet<>();

	/**
	 * Options used when generating project changelog.
	 *
	 * @see GenerateChangelogTask
	 */
	public Map<GenerateChangelogOptions, Object> generateChangelogOptions = new HashMap<>();

	/**
	 * Exclude given resource directory paths for {@code media} module.
	 * Note that this method overrides existing exclude directories.
	 *
	 * @param dirPaths directory paths to exclude.
	 */
	public void excludeResourceDirs(String... dirPaths) {

		excludedResourceDirs.clear();
		excludedResourceDirs.addAll(Arrays.asList(dirPaths));
	}

	/**
	 * Returns a {@code Set} of excluded resource directory paths.
	 *
	 * @return <i>unmodifiable</i> {@code Set} of excluded directory paths.
	 */
	@UnmodifiableView
	@Contract(pure = true)
	public Set<String> getExcludedResourceDirs() {
		return Collections.unmodifiableSet(excludedResourceDirs);
	}

	/**
	 * Returns {@code true} if given path is an excluded resource, {@code false} otherwise.
	 */
	@Contract(pure = true)
	public boolean isExcludedResource(String path) {
		return excludedResourceDirs.contains(path);
	}
}
