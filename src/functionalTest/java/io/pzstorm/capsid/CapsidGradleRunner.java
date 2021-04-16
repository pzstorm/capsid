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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gradle.internal.impldep.org.jetbrains.annotations.Contract;
import org.gradle.testkit.runner.internal.DefaultGradleRunner;

public class CapsidGradleRunner extends DefaultGradleRunner {

	public static CapsidGradleRunner create() {
		return new CapsidGradleRunner();
	}

	@Override
	@Contract(value = "_ -> this")
	public CapsidGradleRunner withArguments(String... arguments) {

		List<String> argumentList = new ArrayList<>(getArguments());
		argumentList.addAll(Arrays.asList(arguments));
		return (CapsidGradleRunner) this.withArguments(argumentList);
	}
}
