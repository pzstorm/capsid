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
package io.pzstorm.capsid.dist;

import java.util.Arrays;

import org.gradle.api.InvalidUserDataException;

/**
 * <p>These options are used to configure {@code github-changelog-generator} CL application.</p>
 * Run {@link #HELP} command to get an up-to-date list of configuration options.
 */
@SuppressWarnings("unused")
public enum GenerateChangelogOptions {

	/**
	 * Username of the owner of target GitHub repo.
	 */
	USER("u", "user"),

	/**
	 * Name of project on GitHub.
	 */
	PROJECT("p", "project"),

	/**
	 * To make more than 50 requests per hour your GitHub token is required.
	 *
	 * @see <a href="https://github.com/settings/tokens/new">Generate new token on Github</a>
	 */
	TOKEN("t", "token"),

	/**
	 * Date format. Default is {@code %Y-%m-%d}.
	 */
	FORMAT("f", "date-format"),

	/**
	 * Output file. To print to STDOUT instead, use blank as path.
	 * Default is {@code CHANGELOG.md}.
	 */
	OUTPUT("o", "output"),

	/**
	 * Optional base file to append generated changes to.
	 */
	BASE("b", "base"),

	/**
	 * Set up custom label for the release summary section.
	 * Default is "".
	 */
	SUMMARY_LABEL("summary-label"),

	/**
	 * Set up custom label for the breaking changes section.
	 * Default is {@code **Breaking changes:**}.
	 */
	BREAKING_LABEL("breaking-label"),

	/**
	 * Set up custom label for enhancements section.
	 * Default is {@code **Implemented enhancements:**"}.
	 */
	ENHANCEMENT_LABEL("enhancement-label"),

	/**
	 * Set up custom label for bug-fixes section.
	 * Default is {@code **Fixed bugs:**}.
	 */
	BUGS_LABEL("bugs-label"),

	/**
	 * Set up custom label for the deprecated changes section.
	 * Default is {@code **Deprecated:**}.
	 */
	DEPRECATED_LABEL("deprecated-label"),

	/**
	 * Set up custom label for the removed changes section.
	 * Default is {@code **Removed:**}.
	 */
	REMOVED_LABEL("removed-label"),

	/**
	 * Set up custom label for the security changes section.
	 * Default is {@code **Security fixes:**}.
	 */
	SECURITY_LABEL("security-label"),

	/**
	 * Set up custom label for closed-issues section.
	 * Default is {@code **Closed issues:**}.
	 */
	ISSUES_LABEL("issues-label"),

	/**
	 * Set up custom header label.
	 * Default is {@code # Changelog}.
	 */
	HEADER_LABEL("header-label"),

	/**
	 * Define your own set of sections which overrides all default sections.
	 */
	CONFIGURE_SECTIONS("configure-sections"),

	/**
	 * Add new sections but keep the default sections.
	 */
	ADD_SECTIONS("add-sections"),

	/**
	 * Add YAML front matter. Formatted as JSON because it's easier to add on the command line.
	 */
	FRONT_MATTER("front-matter"),

	/**
	 * Set up custom label for pull requests section.
	 * Default is {@code **Merged pull requests:**}.
	 */
	PR_LABEL("pr-label"),

	/**
	 * Include closed issues in changelog.
	 */
	ISSUES("[no-]issues"),

	/**
	 * Include closed issues without labels in changelog.
	 * Default is {@code true}.
	 */
	ISSUES_WITHOUT_LABELS("[no-]issues-wo-labels"),

	/**
	 * Include pull requests without labels in changelog.
	 * Default is {@code true}.
	 */
	PR_WITHOUT_LABELS("[no-]pr-wo-labels"),

	/**
	 * Include pull-requests in changelog.
	 * Default is {@code true}.
	 */
	PULL_REQUESTS("[no-]pull-requests"),

	/**
	 * Use milestone to detect when issue was resolved.
	 * Default is {@code true}.
	 */
	FILTER_BY_MILESTONE("[no-]filter-by-milestone"),

	/**
	 * Add author of pull request at the end.
	 * Default is {@code true}.
	 */
	AUTHOR("[no-]author"),

	/**
	 * Use GitHub tags instead of Markdown links for the author of an issue or pull-request.
	 */
	ALT_USERNAMES("usernames-as-github-logins"),

	/**
	 * Generate log from unreleased closed issues only.
	 */
	UNRELEASED_ONLY("unreleased-only"),

	/**
	 * Add to log unreleased closed issues.
	 * Default is {@code true}.
	 */
	UNRELEASED("[no-]unreleased"),

	/**
	 * Set up custom label for unreleased closed issues section.
	 * Default is {@code **Unreleased:**}.
	 */
	UNRELEASED_LABEL("unreleased-label"),

	/**
	 * Include compare link (Full Changelog) between older version and newer version.
	 * Default is {@code true}.
	 */
	COMPARE_LINK("[no-]compare-link"),

	/**
	 * Of the labeled issues, only include the ones with the specified labels.
	 */
	INCLUDE_LABELS("include-labels"),

	/**
	 * Issues with the specified labels will be excluded from changelog.
	 * Default is {@code 'duplicate,question,invalid,wontfix'}.
	 */
	EXCLUDE_LABELS("exclude-labels"),

	/**
	 * Issues with these labels will be added to a new section, called "Release Summary".
	 * The section display only body of issues.
	 * Default is {@code 'release-summary,summary'}.
	 */
	SUMMARY_LABELS("summary-labels"),

	/**
	 * Issues with these labels will be added to a new section, called "Breaking changes".
	 * Default is {@code 'backwards-incompatible,breaking'}.
	 */
	BREAKING_LABELS("breaking-labels"),

	/**
	 * Issues with the specified labels will be added to "Implemented enhancements" section.
	 * Default is {@code 'enhancement,Enhancement'}.
	 */
	ENHANCEMENT_LABELS("enhancement-labels"),

	/**
	 * Issues with the specified labels will be added to "Fixed bugs" section.
	 * Default is {@code 'bug,Bug'}.
	 */
	BUG_LABELS("bug-labels"),

	/**
	 * Issues with the specified labels will be added to a section called "Deprecated".
	 * Default is {@code 'deprecated,Deprecated'}.
	 */
	DEPRECATED_LABELS("deprecated-labels"),

	/**
	 * Issues with the specified labels will be added to a section called "Removed".
	 * Default is {@code 'removed,Removed'}.
	 */
	REMOVED_LABELS("removed-labels"),

	/**
	 * Issues with the specified labels will be added to a section called "Security fixes".
	 * Default is {@code 'security,Security'}.
	 */
	SECURITY_LABELS("security-labels"),

	/**
	 * The specified labels will be shown in brackets next to each matching issue.
	 * Use "ALL" to show all labels. Default is {@code []}.
	 */
	ISSUE_LINE_LABELS("issue-line-labels"),

	/**
	 * Changelog will exclude specified tags
	 */
	EXCLUDE_TAGS("exclude-tags"),

	/**
	 * Apply a regular expression on tag names so that they can be excluded,
	 * for example: {@code --exclude-tags-regex ".*+d{1,}"}.
	 */
	EXCLUDE_TAGS_REGEX("exclude-tags-regex"),

	/**
	 * Changelog will start after specified tag.
	 */
	SINCE_TAG("since-tag"),

	/**
	 * Changelog will end before specified tag.
	 */
	DUE_TAG("due-tag"),

	/**
	 * Maximum number of issues to fetch from GitHub.
	 * Default is {@code unlimited}.
	 */
	MAX_ISSUES("max-issues"),

	/**
	 * The URL to point to for release links, in printf format (with the tag as variable).
	 */
	RELEASE_URL("release-url"),

	/**
	 * The Enterprise GitHub site where your project is hosted.
	 */
	GITHUB_SITE("github-site"),

	/**
	 * The enterprise endpoint to use for your GitHub API.
	 */
	GITHUB_API("github-api"),

	/**
	 * Create a simple list from issues and pull requests.
	 * Default is {@code false}.
	 */
	SIMPLE_LIST("simple-list"),

	/**
	 * Put the unreleased changes in the specified release number.
	 */
	FUTURE_RELEASE("future-release"),

	/**
	 * Limit pull requests to the release branch, such as master or release.
	 */
	RELEASE_BRANCH("release-branch"),

	/**
	 * Use HTTP Cache to cache GitHub API requests (useful for large repos).
	 * Default is {@code true}.
	 */
	HTTP_CACHE("[no-]http-cache"),

	/**
	 * Filename to use for cache.
	 * Default is {@code github-changelog-http-cache} in a temporary directory.
	 */
	CACHE_FILE("cache-file"),

	/**
	 * Filename to use for cache log.
	 * Default is {@code github-changelog-logger.log} in a temporary directory.
	 */
	CACHE_LOG("cache-log"),

	/**
	 * Path to {@code cacert.pem} file. Respects SSL_CA_PATH.
	 * Default is a bundled {@code lib/github_changelog_generator/ssl_certs/cacert.pem}.
	 */
	SSL_CA_FILE("ssl-ca-file"),

	/**
	 * Path to Ruby file(s) to require before generating changelog.
	 */
	REQUIRE("require"),

	/**
	 * Run verbosely. Default is true.
	 */
	VERBOSE("[no-]verbose"),

	/**
	 * Print version number.
	 */
	VERSION("v", "version"),

	/**
	 * Displays Help.
	 */
	HELP("h", "help");

	private final String shortOpt, longOpt;

	GenerateChangelogOptions(String shortOpt, String longOpt) {

		this.shortOpt = shortOpt;
		this.longOpt = longOpt;
	}

	GenerateChangelogOptions(String longOpt) {
		this("", longOpt);
	}

	/**
	 * Returns a formatted option based on given arguments.
	 *
	 * @param args array of arguments to use when formatting.
	 * @return formatted option ready to be used as a command option.
	 */
	public String formatOption(String... args) {

		String option = '-' + shortOpt;
		if (shortOpt.isEmpty())
		{
			if (longOpt.startsWith("[no-]"))
			{
				if (args.length == 0 || !(args[0].equals("false") || args[0].equals("true")))
				{
					String format = "Invalid changelog option argument '%s', expected a boolean";
					throw new InvalidUserDataException(String.format(format, Arrays.toString(args)));
				}
				option = "--" + (args[0].equals("true") ?
						longOpt.substring(5) : "no-" + longOpt.substring(5));
			}
			else option = "--" + longOpt;
		}
		return option;
	}
}
