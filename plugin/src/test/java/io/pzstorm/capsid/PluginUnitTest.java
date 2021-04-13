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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

@Tag("unit")
public abstract class PluginUnitTest {

	protected static final File WORKSPACE = new File("build/tmp/unitTest");

	@BeforeAll
	static void createWorkspaceDirectory() throws IOException {

		if (!WORKSPACE.exists()) {
			Files.createDirectory(WORKSPACE.toPath());
		}
		RecursiveDeleteOption option = RecursiveDeleteOption.ALLOW_INSECURE;
		MoreFiles.deleteDirectoryContents(WORKSPACE.toPath(), option);
	}
}
