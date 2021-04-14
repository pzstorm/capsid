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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This object represents a Unix-style path that uses <i>forward slashes</i>. This contrasts with paths
 * in MS-DOS and similar operating systems (such as FreeDOS) and the Microsoft Windows operating systems,
 * in which directories and files are separated  with backslashes. The backslash is an upward-to-the-left
 * sloping straight line character that is a mirror image of the forward slash.
 * <ul style="list-style-type:none">
 *     <li>Unix path: {@code C:/path/to/file}</li>
 *     <li>Windows path: {@code C:\path\to\file}</li>
 * </ul>
 *
 * @see <a href=http://www.linfo.org/forward_slash.html>Forward Slash Definition</a>
 */
@SuppressWarnings("WeakerAccess")
public class UnixPath {

	private final String path;

	private UnixPath(Path path) {
		this.path = convert(path);
	}

	/**
	 * Convert a given {@code File} path into a <i>Unix-style</i> path.
	 */
	public static UnixPath get(File file) {
		return new UnixPath(file.toPath());
	}

	/**
	 * Convert a given {@code Path} into a <i>Unix-style</i> path.
	 */
	public static UnixPath get(Path path) {
		return new UnixPath(path);
	}

	/**
	 * Convert a given path into a <i>Unix-style</i> path.
	 *
	 * @throws java.nio.file.InvalidPathException if the path
	 *        {@code String}cannot be converted to a {@code Path}
	 */
	public static UnixPath get(String path) {
		return new UnixPath(Paths.get(path));
	}

	/**
	 * Convert the given path to a standard Java {@code Path}.
	 * Use this method when you don't want to instantiate a {@code UnixPath}
	 * object and just want a quick conversion to a <i>Unix-style</i> path.
	 */
	public static String convert(Path path) {
		return path.toString().replace("\\", "/");
	}

	/**
	 * Convert this path to a standard Java {@code Path}.
	 */
	public Path convert() {
		return Paths.get(path);
	}

	/**
	 * @return a {@code String} representation of this path.
	 */
	@Override
	public String toString() {
		return path;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return path.equals(((UnixPath) o).path);
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}
}
