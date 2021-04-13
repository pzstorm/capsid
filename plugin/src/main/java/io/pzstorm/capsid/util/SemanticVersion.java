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
 * This object represents a valid semantic version.
 *
 * @see <a href="https://semver.org/">Semantic Versioning</a>
 */
public class SemanticVersion {

	private static final Pattern SEM_VER = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(-(.*))?$");

	public final Integer major, minor, patch;
	public final String classifier;

	/**
	 * Creates a new semantic version instance from given {@code String}.
	 *
	 * @throws InvalidUserDataException if given semantic version is malformed.
	 */
	public SemanticVersion(String version) {

		Matcher matcher = SEM_VER.matcher(version);
		if (!matcher.find())
		{
			throw new InvalidUserDataException("Malformed semantic version '" + version + '\'');
		}
		this.major = Integer.valueOf(matcher.group(1));
		this.minor = Integer.valueOf(matcher.group(2));
		this.patch = Integer.valueOf(matcher.group(3));

		String sClassifier = matcher.group(4);
		this.classifier = sClassifier != null ? matcher.group(5) : "";
	}

	@Override
	public String toString() {
		return String.format("%d.%d.%d%s", major, minor, patch, classifier);
	}

	public static class Comparator implements java.util.Comparator<SemanticVersion> {

		@Override
		public int compare(SemanticVersion o1, SemanticVersion o2) {

			if (!o1.major.equals(o2.major))
			{
				return o1.major.compareTo(o2.major);
			}
			if (!o1.minor.equals(o2.minor))
			{
				return o1.minor.compareTo(o2.minor);
			}
			return o1.patch.compareTo(o2.patch);
		}
	}
}
