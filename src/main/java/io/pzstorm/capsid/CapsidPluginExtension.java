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

import java.net.URL;
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

	/**
	 * {@code Set} of resource directories to exclude from source set.
	 */
	private final Set<String> excludedResourceDirs = new HashSet<>();

	/**
	 * Determines whether the consumer project is treated as a mod project.
	 * This property should be set to {@code false} if your project is not a mod project.
	 */
	public boolean isModProject = true;

	/**
	 * Options used when generating project changelog.
	 *
	 * @see GenerateChangelogTask
	 */
	public Map<GenerateChangelogOptions, Object> generateChangelogOptions = new HashMap<>();

	/**
	 * <p>
	 * Name of the Github repository owner that hosts the project.
	 * This property is used to generate project changelog and is
	 * normally extracted from {@code url} mod property.
	 * </p>
	 * For example if the {@link URL} to repository was:
	 * <blockquote>
	 * <code>https://github.com/pzmodder/pz-mod</code>
	 * </blockquote>
	 * Then the name of the owner would be {@code pzmodder}.
	 *
	 * @see GenerateChangelogTask
	 */
	private String repositoryOwner = "";

	/**
	 * <p>
	 * Name of the Github repository that hosts the project.
	 * This property is used to generate project changelog and is
	 * normally extracted from {@code url} mod property.
	 * </p>
	 * For example if the {@link URL} to repository was:
	 * <blockquote>
	 * <code>https://github.com/pzmodder/pz-mod</code>
	 * </blockquote>
	 * Then the name of the repository would be {@code pz-mod}.
	 * * @see GenerateChangelogTask
	 */
	private String repositoryName = "";

	/**
	 * Returns the owner of the Github repository where the project is hosted.
	 *
	 * @return value of configure property or an empty {@code String}
	 * 		if property has not been configured by user.
	 */
	public String getProjectRepositoryOwner() {
		return repositoryOwner;
	}

	/**
	 * Returns the name of the Github repository where the project is hosted.
	 *
	 * @return value of configure property or an empty {@code String}
	 * 		if property has not been configured by user.
	 */
	public String getProjectRepositoryName() {
		return repositoryName;
	}

	/**
	 * Set the owner and name of the Github repository where the project is hosted.
	 *
	 * @param owner name of the Github repository owner that hosts the project.
	 * @param name name of the Github repository that hosts the project.
	 *
	 * @see GenerateChangelogTask
	 */
	public void setProjectRepository(String owner, String name) {

		repositoryOwner = owner;
		repositoryName = name;
	}

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
