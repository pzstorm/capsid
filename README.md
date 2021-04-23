# Storm Capsid

[![Java CI](https://github.com/pzstorm/capsid/actions/workflows/java-ci.yaml/badge.svg?branch=dev)](https://github.com/pzstorm/capsid/actions/workflows/java-ci.yaml) [![Plugin Portal](https://img.shields.io/maven-metadata/v?label=plugin&color=blue&logo=gradle&metadataUrl=https://plugins.gradle.org/m2/io/pzstorm/capsid/io.pzstorm.capsid.gradle.plugin/maven-metadata.xml)](https://plugins.gradle.org/plugin/io.pzstorm.capsid) [![License](https://img.shields.io/github/license/pzstorm/capsid?logo=gnu)](https://www.gnu.org/licenses/) [![Discord](https://img.shields.io/discord/823907021178798150?color=7289DA&label=discord&logo=discord&logoColor=white)](https://discord.gg/ZCmg9VsvSW)

Capsid is a compact mod development environment for [Project Zomboid](https://projectzomboid.com/blog/).

It is [Gradle](https://gradle.org/) plugin that enables powerful [IDE](https://en.wikipedia.org/wiki/Integrated_development_environment) features and improves your modding workflow. It helps automate the process of setting up, assembling and deploying your project.

![Capsid](https://raw.githubusercontent.com/pzstorm/capsid/gh-pages/capsid-banner.png)

## Introduction

Whether you are a creating textures, models, maps or writing code you are working with often large and complex collections of files. These files need to be interpreted, and (in case of mod files) version controlled. Since we are human beings, we are not nearly as good at interpreting and storing raw data as machines are. This is why we need their help through advanced tools such as [Git](https://git-scm.com/) and IDE's. Git helps manage our mod versions, while an IDE provides powerful features to increase efficiency, offer code assistance, and make development more enjoyable.

- Increased efficiency means an overall higher mod quality.  
- Code analysis helps spot bugs and avoid lengthy debugging sessions. 
- Code navigation helps quickly find what we are looking for saving us time and energy.
- More enjoyable workflow brings more motivation used to create amazing mods.

Capsid serves as an umbrella for these tools, connecting everything you need in one system.

## Features

- Decompiles and packages game classes to expose game engine code.
- Uses [ZomboidDoc](https://github.com/yooksi/pz-zdoc/) to compile a readable and always up-to-date modding Lua library.
- Fully automated project changelog generation.
- Create mod distributions with a click of a button.
- Fully integrates with IntelliJ IDEA.

## Where do I get it?

Check the latest version on [Gradle Plugin Portal](https://plugins.gradle.org/plugin/io.pzstorm.capsid) and declare the plugin like this:

```groovy
plugins {
    id 'io.pzstorm.capsid' version '0.2.0' 
}
```

## How to use it

### Project license

It is important to license your project with an appropriate open source license to ensure others have the right to copy, distribute or modify your work without being at risk of take-downs, shake-downs, or litigation.

Learn what happens when [you choose not to license your project](https://choosealicense.com/no-permission/).

Capsid mod template comes with MIT license for your project to use. Keep in mind that you can choose to license your project under a different license at any point in time. To apply the template license provided in the distribution to your project follow these steps:

- Update copyright in `LICENSE.txt` to include the current year and your name:

		Copyright (c) [year] [fullname]


- Update copyright in `README.md` under 'License' section to include your name and (optionally) a link to your Github profile, personal website, or another place online you can be reached:

		MIT © [Yourname](https://github.com/username)

Note that you can choose to include your full name or your Github username. It is a matter of preference and legally speaking both should be valid as long as you can prove the identity provided represents you. 

### Discord integration

If you are a Discord user and want to let the world know that you are working on Project Zomboid mods you can do so with IntelliJ IDEA Discord integration.

Do the following steps to enable Discord integration for your mod project:

- Install [Discord Integration](https://plugins.jetbrains.com/plugin/10233-discord-integration) IDEA plugin from the marketplace.
- Run `createDiscordIntegration` task.[<sup>?</sup>](#discord-integration "This task will run automatically when you run setupWorkspace configuration.")

Project name and description displayed in Rich Presence will be read from `mod.info` file so you should run this task after initializing mod. Note that you can rerun the task at any time if you update mod metadata or accidentally delete the configuration file.[<sup>?</sup>](#a ".idea/discord.xml")

### Search scopes

IDEA [scopes](https://www.jetbrains.com/help/idea/settings-scopes.html) are sets of files you can search in various contexts. Capsid generates custom search scopes to help you find code usages or references helpful in modding the game:

- `mod-lua` - All Lua files in `media` directory.
- `mod-media` - All files in `media` directory.
- `pz-java` - Project Zomboid Java classes.
- `pz-lua` - Project Zomboid Lua classes.

Learn more about [searching everywhere](https://www.jetbrains.com/help/idea/searching-everywhere.html) in IntelliJ IDEA.

### Changelog

> If you are not familiar with what a changelog is I recommend reading [keep a changelog](https://keepachangelog.com/en/1.0.0/).

Capsid uses [github-changelog-generator](https://github.com/github-changelog-generator/github-changelog-generator) to generate standardized changelogs. Your should generate a changelog after each release, when all issues on project Github repository have been closed with a merge commit.

Before generating a changelog you need to do the following:

- Set Github repository owner and name through Capsid plugin extension:

  ```groovy
  setProjectRepository('repo-owner', 'repo-name')
  ```

  If the named properties are not configured by user, Capsid will try to read the repository owner and name information from `url` property in `mod.info` file assuming it is a valid Github URL.

- Generate a [Github token](https://github.com/github-changelog-generator/github-changelog-generator#github-token) and store it as  a project property in `local.properties`:

	```properties
	gcl.token=<your-40-digit-token>
	```
	
	You can also store the token as an IDEA terminal environment variable `CHANGELOG_GITHUB_TOKEN`.<a href="https://www.jetbrains.com/help/idea/settings-tools-terminal.html"><sup>?</sup></a>

Then simply run `generateChangelog` task to generate project changelog.

### Distribution

Before others can download your mod you need to assemble and upload the mod distribution. 

Assembling distributions is a process of packaging everything your mod needs to run in production environment in compressed archives. Anything not needed in production environment (such as gradle files) needs to be excluded from distributions. 

Capsid handles this for you. Just run `assembleDist` and a distribution archive matching the current project version will be created in `build/distributions` directory.

## List of tasks

### Setup tasks

- `initLocalProperties` - Initialize local project properties.
- `createLaunchRunConfigs` - Create game launch run configurations.
- `createModSearchScopes` - Create IDEA search scopes for mod files.
- `createDiscordIntegration` - Show IDEA project in Discord via rich presence.

### Zomboid tasks

- `zomboidClasses` - Assembles Project Zomboid classes.
- `decompileZomboid` - Decompile Project Zomboid classes.
- `zomboidJar` - Assembles a jar archive containing game classes.
- `zomboidSourcesJar` - Assembles a jar containing decompiled game sources.
- `zomboidLuaJar` - Assembles a jar containing compiled Lua classes.
- `zomboidVersion` - Save and print Project Zomboid game version.
- `annotateZomboidLua` - Annotate vanilla Lua with EmmyLua.
- `compileZomboidLua` - Compile Lua library from modding API.
- `updateZomboidLua` - Run ZomboidDoc to update compiled Lua library.

### Mod tasks

- `createModStructure` - Create default mod directory structure.
- `saveModMetadata` - Save mod metadata to file.
- `loadModMetadata` - Load mod metadata information.
- `initModMetadata` - Initialize mod metadata information.
- `showModMetadata` - Print mod metadata information.
- `applyModTemplate` - Apply Project Zomboid mod template.

### Distribution tasks

- `generateChangelog` - Generate a project changelog.
- `mediaClasses` - Assembles mod Lua classes.
- `processMediaResources` - Process mod resources.

## License

This project is licensed under [GNU General Public License v3.0](https://github.com/cocolabs/pz-zdoc/blob/master/LICENSE.txt).