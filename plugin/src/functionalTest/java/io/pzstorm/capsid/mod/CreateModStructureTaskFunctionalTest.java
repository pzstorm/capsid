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
package io.pzstorm.capsid.mod;

import java.io.File;
import java.util.*;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginFunctionalTest;
import io.pzstorm.capsid.setup.LocalProperties;
import io.pzstorm.capsid.util.UnixPath;

class CreateModStructureTaskFunctionalTest extends PluginFunctionalTest {

	@Test
	void shouldCreateCorrectModStructureFromSourceSet() {

		GradleRunner runner = getRunner();
		List<String> arguments = new ArrayList<>(runner.getArguments());
		arguments.add(ModTasks.CREATE_MOD_STRUCTURE.name);

		BuildResult result = getRunner().withArguments(arguments).build();
		assertTaskOutcomeSuccess(result, ModTasks.CREATE_MOD_STRUCTURE.name);

		UnixPath gameDir = Objects.requireNonNull(LocalProperties.GAME_DIR.findProperty(getProject()));

		Set<String> expectedDirNames = new HashSet<>();
		Arrays.stream(gameDir.convert().resolve("media").toFile().listFiles(File::isDirectory))
				.forEach(f -> expectedDirNames.add(f.getName()));

		Set<String> actualDirNames = new HashSet<>();
		Arrays.stream(getProjectDir().toPath().resolve("media").toFile().listFiles(File::isDirectory))
				.forEach(f -> actualDirNames.add(f.getName()));

		Assertions.assertEquals(expectedDirNames, actualDirNames);
	}
}