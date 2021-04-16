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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

public class Utils {

	/**
	 * Deletes the given directory and all files contained within it recursively.
	 *
	 * @param directory {@code File} directory to delete.
	 *
	 * @throws IOException if path or any file in the subtree rooted at it can't be deleted for any reason.
	 */
	public static void deleteDirectory(File directory) throws IOException {
		MoreFiles.deleteRecursively(directory.toPath(), RecursiveDeleteOption.ALLOW_INSECURE);
	}

	/**
	 * Read the resource from given path as a byte array and write to specified {@code File}.
	 *
	 * @param clazz {@code Class} to get the {@code ClassLoader} for.
	 * @param path path to the resource to read.
	 * @param file {@code File} to write the stream to.
	 *
	 * @throws IOException if unable to find resource for given path or an I/O error
	 * 		occurred while retrieving or writing resource as stream.
	 */
	public static void readResourceAsFileFromStream(Class<?> clazz, String path, File file) throws IOException {

		try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream(path))
		{
			if (inputStream == null) {
				throw new IOException("Unable to find resource for path '" + path + '\'');
			}
			java.nio.file.Files.write(file.toPath(), ByteStreams.toByteArray(inputStream));
		}
	}

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
			InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			return new BufferedReader(streamReader).lines().collect(Collectors.joining("\n"));
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

	/**
	 * Finds the resource {@code File} with the given path. A resource is some data (images, audio, text, etc)
	 * that can be accessed by class code in a way that is independent of the location of the code.
	 *
	 * @param path path to resource.
	 *
	 * @throws FileNotFoundException if the resource was not found.
	 * @see ClassLoader#getResource(String)
	 */
	public static File getFileFromResources(String path) throws FileNotFoundException {

		URL resource = Utils.class.getClassLoader().getResource(path);
		if (resource == null) {
			throw new FileNotFoundException("Unable to find resource for path '" + path + '\'');
		}
		try {
			return new File(Objects.requireNonNull(resource).toURI());
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Unzip the given {@code Zip} archive to destination directory.
	 *
	 * @param archive {@code Zip} archive to unzip.
	 * @param destination destination directory to unzip to.
	 *
	 * @throws IOException if an I/O error occurred while unzipping archive.
	 */
	public static void unzipArchive(File archive, File destination) throws IOException {

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archive)))
		{
			byte[] buffer = new byte[1024];
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null)
			{
				File newFile = new File(destination, zipEntry.getName());
				if (!zipEntry.isDirectory())
				{
					// fix for Windows-created archives
					File parent = newFile.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException("Failed to create directory " + parent);
					}
					// write file content
					try (FileOutputStream fos = new FileOutputStream(newFile))
					{
						//@formatter:off
						int len; while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						} //@formatter:on
					}
				}
				else if (!newFile.isDirectory() && !newFile.mkdirs()) {
					throw new IOException("Failed to create directory " + newFile);
				}
				zis.closeEntry();
				zipEntry = zis.getNextEntry();
			}
		}
	}
}
