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

import org.gradle.internal.os.OperatingSystem;

@SuppressWarnings("SpellCheckingInspection")
public class VmParameter {

	/** Controls whether the game should start in debug mode. */
	public final boolean isDebug;

	/** Launch the game with Steam support to have access to workshop. */
	public final boolean steamIntegration;

	/** Zomboid network logging level. */
	public final int zNetLog;

	/** Enables invoking of concurrent GC by using the {@code System.gc()} request. */
	public final boolean useConcMarkSweepGC;

	/** Enables the dumping of mini-dumps upon fatal errors on Windows platform. */
	public final boolean createMinidumpOnCrash;

	/** Disables completely the use of pre-allocated exception. */
	public final boolean omitStackTraceInFastThrow;

	/** Paths for native libraries to be loaded from. */
	public final String[] javaLibraryPaths;

	/** Paths for {@code LWJGL} libraries to be loaded from. */
	public final String[] lwjglLibraryPaths;

	/** Initial memory allocation pool for Java Virtual Machine. */
	public final int xms;

	/** Maximum memory allocation pool for Java Virtual Machine. */
	public final int xmx;

	private VmParameter(Builder builder) {

		this.isDebug = builder.isDebug;
		this.steamIntegration = builder.steamIntegration;
		this.zNetLog = builder.zNetLog;
		this.useConcMarkSweepGC = builder.useConcMarkSweepGC;
		this.createMinidumpOnCrash = builder.createMinidumpOnCrash;
		this.omitStackTraceInFastThrow = builder.omitStackTraceInFastThrow;
		this.javaLibraryPaths = builder.javaLibraryPaths;
		this.lwjglLibraryPaths = builder.lwjglLibraryPaths;
		this.xms = builder.xms;
		this.xmx = builder.xmx;
	}

	/**
	 * Format an advanced JVM runtime option with the given name and flag.
	 * <p>
	 * Boolean options are used to either enable a feature that is disabled by default
	 * or disable a feature that is enabled by default. Such options do not require a parameter.
	 * </p><p>
	 * Boolean -XX options are enabled using the plus sign {@code (-XX:+OptionName)}
	 * and disabled using the minus sign {@code (-XX:-OptionName)}.
	 * </p>
	 *
	 * @param name name of the option to format.
	 * @param flag whether to enable or disable the option.
	 * @return formatted advanced JVM runtime option string.
	 *
	 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/tools/windows/java.html">
	 * 		Java Platform, Standard Edition Tools Reference</a>
	 */
	public static String formatAdvancedRuntimeOption(String name, boolean flag) {
		return String.format("-XX:%c%s", flag ? '+' : '-', name);
	}

	/**
	 * Returns delimiter to be used to separate {@code VMParamter} paths
	 * depending on the operating system platform the user is on.
	 * Use {@code ;} on Windows and {@code :} on other platforms.
	 */
	public static String getPathDelimiter() {
		return OperatingSystem.current() == OperatingSystem.WINDOWS ? ";" : ":";
	}

	@Override
	public String toString() {

		String expOptions = String.join(" ", new String[]{
				formatAdvancedRuntimeOption("UseConcMarkSweepGC", useConcMarkSweepGC),
				formatAdvancedRuntimeOption("CreateMinidumpOnCrash", createMinidumpOnCrash),
				formatAdvancedRuntimeOption("OmitStackTraceInFastThrow", omitStackTraceInFastThrow)
		});
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("-Ddebug=%d -Dzomboid.steam=%d -Dzomboid.znetlog=%d %s -Xms%dm -Xmx%dm",
				isDebug ? 1 : 0, steamIntegration ? 1 : 0, zNetLog, expOptions, xms, xmx
		));
		String delimiter = getPathDelimiter();
		if (javaLibraryPaths.length > 0)
		{
			sb.append(" -Djava.library.path=").append(javaLibraryPaths[0]);
			if (javaLibraryPaths.length > 1)
			{
				for (int i = 1; i < javaLibraryPaths.length; i++) {
					sb.append(delimiter).append(javaLibraryPaths[i]);
				}
			}
		}
		if (lwjglLibraryPaths.length > 0)
		{
			sb.append(" -Dorg.lwjgl.librarypath=").append(lwjglLibraryPaths[0]);
			if (lwjglLibraryPaths.length > 1)
			{
				for (int i = 1; i < lwjglLibraryPaths.length; i++) {
					sb.append(delimiter).append(lwjglLibraryPaths[i]);
				}
			}
		}
		return sb.toString();
	}

	//@formatter:off
	public static class Builder {

		private boolean isDebug = false;
		private boolean steamIntegration = true;
		private int zNetLog = 1;

		private boolean useConcMarkSweepGC = true;
		private boolean createMinidumpOnCrash = false;
		private boolean omitStackTraceInFastThrow = false;

		private String[] javaLibraryPaths = new String[0];
		private String[] lwjglLibraryPaths = new String[0];

		private int xms = 1800;
		private int xmx = 2048;

		/**
		 * Launch the game in debug mode.
		 */
		public Builder withDebug(boolean debug) {
			this.isDebug = debug;
			return this;
		}

		/**
		 * Launch the game with Steam support to have access to workshop.
		 */
		public Builder withSteamIntegration(boolean steam) {
			this.steamIntegration = steam;
			return this;
		}

		/**
		 * Set Zomboid network logging level.
		 */
		public Builder withNetworkLogging(int logLevel) {
			this.zNetLog = logLevel;
			return this;
		}

		/**
		 * Enable invoking of concurrent GC by using the {@code System.gc()} request.
		 */
		public Builder withConcurrentMarkSweepCollection(boolean flag) {
			this.useConcMarkSweepGC = flag;
			return this;
		}

		/**
		 * Enable the dumping of minidumps upon fatal errors on Windows platform.
		 */
		public Builder withMinidumpOnCrash(boolean flag) {
			this.createMinidumpOnCrash = flag;
			return this;
		}

		/**
		 * Disable completely the use of pre-allocated exception.
		 */
		public Builder withOmittingStackTraceInFastThrow(boolean flag) {
			this.omitStackTraceInFastThrow = flag;
			return this;
		}

		/**
		 * Configure initial memory allocation pool for Java Virtual Machine.
		 */
		public Builder withInitialMemoryAllocation(int size) {
			this.xms = size;
			return this;
		}

		/**
		 * Configure maximum memory allocation pool for Java Virtual Machine.
		 */
		public Builder withMaximumMemoryAllocation(int size) {
			this.xmx = size;
			return this;
		}

		/**
		 * Set paths for native libraries to be loaded from.
		 */
		public Builder withJavaLibraryPaths(String...paths) {
			this.javaLibraryPaths = paths;
			return this;
		}

		/**
		 * Set path for {@code LWJGL} libraries to be loaded from.
		 */
		public Builder withLwjglLibraryPaths(String...paths) {
			this.lwjglLibraryPaths = paths;
			return this;
		}

		/**
		 * Build and return a new {@link VmParameter} instance.
		 */
		public VmParameter build() {
			return new VmParameter(this);
		}
	}//@formatter:on
}
