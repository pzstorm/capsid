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

import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;

public enum Repositories {

	/**
	 * Represents Maven central repository.
	 *
	 * @see RepositoryHandler#mavenCentral()
	 */
	MAVEN_CENTRAL(RepositoryHandler::mavenCentral),

	/**
	 * Represents Maven local cache repository.
	 *
	 * @see RepositoryHandler#mavenLocal()
	 */
	MAVEN_LOCAL(RepositoryHandler::mavenLocal);

	private final RepositorySupplier repository;

	Repositories(RepositorySupplier repository) {
		this.repository = repository;
	}

	/**
	 * Register this repository with the given {@link RepositoryHandler}.
	 */
	public ArtifactRepository register(RepositoryHandler handler) {
		return repository.get(handler);
	}
}
