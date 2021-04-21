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

import java.util.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import org.gradle.api.InvalidUserDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginUnitTest;

class GameVersionTest extends PluginUnitTest {

	@Test
	void whenConstructingMalformedGameVersionShouldThrowException() {

		String[] malformedVersions = new String[]{
				"0", "1.0.0", "0.1.0.0", "0-1", "2$1"
		};
		for (String version : malformedVersions) {
			Assertions.assertThrows(InvalidUserDataException.class, () -> new GameVersion(version));
		}
	}

	@Test
	void shouldConstructGameVersionsWithValidNumbers() {

		Map<String, Integer[]> validVersions = ImmutableMap.of(
				"1.4", new Integer[]{ 1, 4 },
				"0.1", new Integer[]{ 0, 1 },
				"3.5-beta", new Integer[]{ 3, 5 },
				"0.1-alpha$", new Integer[]{ 0, 1 }
		);
		for (Map.Entry<String, Integer[]> entry : validVersions.entrySet())
		{
			GameVersion gameVer = new GameVersion(entry.getKey());
			Integer[] versionData = entry.getValue();

			Assertions.assertEquals(versionData[0], gameVer.major);
			Assertions.assertEquals(versionData[1], gameVer.minor);
		}
	}

	@Test
	void shouldConstructGameVersionsWithValidClassifiers() {

		Map<String, String> validVersions = ImmutableMap.of(
				"0.1-alpha", "alpha",
				"0.4-0rc", "0rc",
				"1.0", ""
		);
		for (Map.Entry<String, String> entry : validVersions.entrySet()) {
			Assertions.assertEquals(entry.getValue(), new GameVersion(entry.getKey()).classifier);
		}
	}

	@Test
	void shouldCorrectlyCompareGameVersions() {

		List<GameVersion> gameVersionList = ImmutableList.of(
				new GameVersion("0.1"),
				new GameVersion("0.2"),
				new GameVersion("0.3"),
				new GameVersion("0.4"),
				new GameVersion("1.5")
		);
		SortedSet<GameVersion> gameVersions = new TreeSet<>(new GameVersion.Comparator());
		gameVersions.addAll(gameVersionList);

		List<GameVersion> gameVerList = new ArrayList<>(gameVersions);
		for (int i = 0; i < gameVerList.size(); i++) {
			Assertions.assertEquals(gameVerList.get(i), gameVersionList.get(i));
		}
	}

	@Test
	void shouldCorrectlyCompareGameVersionUsingEquals() {

		GameVersion A = new GameVersion("0.1");
		GameVersion B = new GameVersion("0.2");
		GameVersion C = new GameVersion("0.3");
		GameVersion D = new GameVersion("0.2");

		Assertions.assertNotEquals(A, B);
		Assertions.assertNotEquals(B, C);
		Assertions.assertNotEquals(C, D);

		Assertions.assertEquals(B, D);
	}

	@Test
	void shouldCorrectlyCompareGameVersionsUsingHashCode() {

		Set<GameVersion> gameVersions = Sets.newHashSet(
				new GameVersion("0.1"),
				new GameVersion("0.2"),
				new GameVersion("0.3")
		);
		Assertions.assertTrue(gameVersions.contains(new GameVersion("0.1")));
		Assertions.assertTrue(gameVersions.contains(new GameVersion("0.2")));
		Assertions.assertTrue(gameVersions.contains(new GameVersion("0.3")));

		Assertions.assertFalse(gameVersions.contains(new GameVersion("0.4")));
	}

	@Test
	void shouldConvertGameVersionToString() {

		Assertions.assertEquals("3.1", new GameVersion("3.1").toString());
		Assertions.assertEquals("2.0-beta", new GameVersion("2.0-beta").toString());
	}
}
