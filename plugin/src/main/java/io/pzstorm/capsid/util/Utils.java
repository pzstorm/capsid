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
package io.pzstorm.capsid.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.io.Files;

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

	/**
	 * Reads text from a given file. The lines do not include line-termination
	 * characters, but do include other leading and trailing whitespace
	 *
	 * @param file {@code File} to read the lines from.
	 * @return a {@code String} representing the contents of given file.
	 *
	 * @throws IOException if an I/O error occurred.
	 * @see Files#readLines(File, Charset)
	 */
	public static String readTextFromFile(File file) throws IOException {
		return String.join("\n", Files.readLines(file, StandardCharsets.UTF_8));
	}
}
