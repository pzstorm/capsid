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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

	/**
	 * Reads the resource for given path and converts it to {@code String}.
	 *
	 * @param clazz {@code Class} to get the {@code ClassLoader} for.
	 * @param path path to the resource to read.
	 *
	 * @throws IOException if unable to find resource for given path or an I/O error
	 * 		occurred while retrieving resource as stream for class loader.
	 */
	public static String readResourceAsTextFromStream(Class<?> clazz, String path) throws IOException {

		try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream(path))
		{
			if (inputStream == null) {
				throw new IOException("Unable to find resource for path '" + path + '\'');
			}
			Stream<String> stream = new BufferedReader(new InputStreamReader(inputStream)).lines();
			return stream.collect(Collectors.joining("\n"));
		}
	}
}
