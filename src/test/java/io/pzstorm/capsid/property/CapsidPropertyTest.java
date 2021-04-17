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
package io.pzstorm.capsid.property;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginUnitTest;

class CapsidPropertyTest extends PluginUnitTest {

	@Test
	void shouldBuildCapsidPropertyWithCorrectValues() {

		String name = "testProperty";
		String comment = "testComment";

		CapsidProperty<String> property = new CapsidProperty.Builder<>(name, String.class)
				.withComment(comment).isRequired(false).build();

		Assertions.assertEquals(name, property.name);
		Assertions.assertEquals(String.class, property.type);
		Assertions.assertEquals(comment, property.comment);
		Assertions.assertFalse(property.required);
	}
}
