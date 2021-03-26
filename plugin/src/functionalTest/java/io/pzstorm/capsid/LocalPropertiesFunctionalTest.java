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
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LocalPropertiesFunctionalTest extends FunctionalTest {

	@Test
	void shouldLoadAllLocalProperties() throws IOException {

		File localProperties = new File(getProjectDir(), "local.properties");

		Map<String, String> localPropertiesMap = new LinkedHashMap<>();
		localPropertiesMap.put("gameDir", "C:/ProjectZomboid/");
		localPropertiesMap.put("ideaHome", "C:/IntelliJ IDEA/");

		String[] localPropertiesLines = new String[localPropertiesMap.size()];
		int i = -1; for (Map.Entry<String, String> entry : localPropertiesMap.entrySet()) {
			localPropertiesLines[i += 1] = String.format("%s=%s", entry.getKey(), entry.getValue());
		}
		writeToFile(localProperties, localPropertiesLines);
		Assertions.assertDoesNotThrow(() -> getRunner().build());

		// load properties for project before asserting
		LocalProperties.load(getProject());

		for (LocalProperties localPropertyEnum : LocalProperties.values()) {
			Assertions.assertNotNull(localPropertyEnum.data.getProperty(getProject()));
		}
	}
}
