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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.pzstorm.capsid.PluginUnitTest;

class VmParameterTest extends PluginUnitTest {

	@Test
	void shouldBuildVmParameterInstanceWithDefaultValues() {

		VmParameter parameters = new VmParameter.Builder().build();

		Assertions.assertFalse(parameters.isDebug);
		Assertions.assertTrue(parameters.steamIntegration);
		Assertions.assertEquals(1, parameters.zNetLog);

		Assertions.assertTrue(parameters.useConcMarkSweepGC);
		Assertions.assertFalse(parameters.createMinidumpOnCrash);
		Assertions.assertFalse(parameters.omitStackTraceInFastThrow);

		Assertions.assertEquals(1800, parameters.xms);
		Assertions.assertEquals(2048, parameters.xmx);
	}

	@Test
	void shouldBuildVmParameterInstanceWithCustomValues() {

		VmParameter parameters = new VmParameter.Builder()
				.withDebug(true)
				.withSteamIntegration(false)
				.withNetworkLogging(0)
				.withConcurrentMarkSweepCollection(false)
				.withMinidumpOnCrash(false)
				.withOmittingStackTraceInFastThrow(false)
				.withInitialMemoryAllocation(250)
				.withMaximumMemoryAllocation(4096)
				.build();

		Assertions.assertTrue(parameters.isDebug);
		Assertions.assertFalse(parameters.steamIntegration);
		Assertions.assertEquals(0, parameters.zNetLog);

		Assertions.assertFalse(parameters.useConcMarkSweepGC);
		Assertions.assertFalse(parameters.createMinidumpOnCrash);
		Assertions.assertFalse(parameters.omitStackTraceInFastThrow);

		Assertions.assertEquals(250, parameters.xms);
		Assertions.assertEquals(4096, parameters.xmx);
	}

	@Test
	void shouldCorrectlyFormatAdvancedRuntimeOption() {

		Map<String, Boolean> advancedOptions = new HashMap<>();
		advancedOptions.put("useConcMarkSweepGC", true);
		advancedOptions.put("createMinidumpOnCrash", false);
		advancedOptions.put("omitStackTraceInFastThrow", true);

		for (Map.Entry<String, Boolean> entry : advancedOptions.entrySet())
		{
			String optionName = entry.getKey();
			boolean flag = entry.getValue();

			String expected = "-XX:" + (flag ? '+' : '-') + optionName;
			String actual = VmParameter.formatAdvancedRuntimeOption(optionName, flag);
			Assertions.assertEquals(expected, actual);
		}
	}
}
