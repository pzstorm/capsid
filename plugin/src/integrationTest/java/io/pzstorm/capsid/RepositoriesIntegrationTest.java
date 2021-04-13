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

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RepositoriesIntegrationTest extends PluginIntegrationTest {

	@Test
	void shouldRegisterRepositories() {

		RepositoryHandler handler1 = getProject(false).getRepositories();
		Map<Repositories, ArtifactRepository> repositoryData = new HashMap<>();

		for (Repositories value : Repositories.values())
		{
			ArtifactRepository repository = value.register(handler1);
			Assertions.assertTrue(handler1.contains(repository));
			repositoryData.put(value, repository);
		}
		Project project = ProjectBuilder.builder().build();
		RepositoryHandler handler2 = project.getRepositories();

		// confirm that repositories are not registered in new project
		for (Repositories value : Repositories.values())
		{
			Assertions.assertFalse(handler2.contains(repositoryData.get(value)));
		}
	}
}
