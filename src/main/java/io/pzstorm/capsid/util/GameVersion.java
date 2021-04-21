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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.api.InvalidUserDataException;

/**
 * This class represents Project Zomboid game version.
 * Unlike {@link SemanticVersion} this version does not use patch numbers in it's format.
 */
public class GameVersion {

	private static final Pattern SEM_VER = Pattern.compile("^(\\d+)\\.(\\d+)(-(.*))?$");

	public final Integer major, minor;
	public final String classifier;

	/**
	 * Creates a new game version instance from given {@code String}.
	 *
	 * @throws InvalidUserDataException if given game version is malformed.
	 */
	public GameVersion(String version) {

		Matcher matcher = SEM_VER.matcher(version);
		if (!matcher.find()) {
			throw new InvalidUserDataException("Malformed game version '" + version + '\'');
		}
		this.major = Integer.valueOf(matcher.group(1));
		this.minor = Integer.valueOf(matcher.group(2));

		String sClassifier = matcher.group(3);
		this.classifier = sClassifier != null ? matcher.group(4) : "";
	}

	@Override
	public String toString() {

		String sClassifier = !classifier.isEmpty() ? '-' + classifier : classifier;
		return String.format("%d.%d%s", major, minor, sClassifier);
	}

	public static class Comparator implements java.util.Comparator<GameVersion> {

		@Override
		public int compare(GameVersion o1, GameVersion o2) {

			if (!o1.major.equals(o2.major)) {
				return o1.major.compareTo(o2.major);
			}
			return o1.minor.compareTo(o2.minor);
		}
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GameVersion that = (GameVersion) o;
		if (new Comparator().compare(this, that) != 0) {
			return false;
		}
		return classifier.equals(that.classifier);
	}

	@Override
	public int hashCode() {

		int result = 31 * major.hashCode() + minor.hashCode();
		return 31 * result + classifier.hashCode();
	}
}
