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

import java.io.*;
import java.util.Properties;

public class LocalProperties extends Properties {

	private static final LocalProperties PROPERTIES = new LocalProperties();
	private static final File FILE = new File("local.properties");

	private LocalProperties() {}

	/**
	 * Load properties from {@code local.properties} file.
	 *
	 * @return {@code true} if properties were successfully loaded, {@code false} otherwise.
	 * @throws RuntimeException when an {@link IOException} occurred while loading file.
	 */
	public static boolean load() {

		if (!FILE.exists())
		{
			CapsidPlugin.LOGGER.warn("WARN: Tried to load local properties, but file does not exist");
			return false;
		}
		// TODO: write integration test to verify properties were loaded
		try (InputStream stream = new FileInputStream(FILE)) {
			PROPERTIES.load(stream);
		}
		catch (IOException e) {
			throw new RuntimeException("An I/O exception occurred while loading local.properties", e);
		}
		return true;
	}

	/** Returns properties {@code File} used to hold local properties. */
	public static File getFile() {
		return FILE;
	}
}
