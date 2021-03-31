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
package io.pzstorm.capsid.setup;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginUnitTest;
import io.pzstorm.capsid.property.CapsidProperty;

class LocalPropertiesTest extends PluginUnitTest {

	@Test
	void shouldReturnFalseWhenLoadingNonExistingLocalProperties() {

		LocalProperties localProperties = LocalProperties.get();

		Assertions.assertTrue(localProperties.getFile(getProject()).delete());
		Assertions.assertFalse(localProperties.load(getProject()));
	}

	@Test
	void shouldGetAllLocalPropertiesByName() {

		LocalProperties localProperties = LocalProperties.get();
		for (CapsidProperty<?> value : localProperties.getProperties()) {
			Assertions.assertEquals(value, localProperties.getProperty(value.name));
		}
	}
}
