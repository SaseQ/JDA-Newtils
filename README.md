<div align="center">
  <img src="assets/img/JDA-Newtils_full_logo.svg" width="60%" alt="DeepSeek-V3" />
</div>
<hr>
<div align="center" style="line-height: 1;">
    <a href="https://jitpack.io/#SaseQ/JDA-Newtils" target="_blank" style="margin: 2px;">
        <img alt="JitPack version" src="https://jitpack.io/v/SaseQ/JDA-Newtils.svg" style="display: inline-block; vertical-align: middle;"/>
    </a>
    <a href="https://discord.gg/5Uvxe5jteM" target="_blank" style="margin: 2px;">
        <img alt="Discord" src="https://img.shields.io/badge/Discord-SaseQcode-7289da?logo=discord&logoColor=white&color=7289da" style="display: inline-block; vertical-align: middle;"/>
    </a>
    <a href="https://github.com/SaseQ/JDA-Newtils" style="margin: 2px;">
        <img alt="Code size" src="https://img.shields.io/github/languages/code-size/SaseQ/JDA-Newtils" style="display: inline-block; vertical-align: middle;"/>
    </a>
    <a href="https://github.com/SaseQ/JDA-Newtils/wiki" style="margin: 2px;">
        <img alt="Wiki docs" src="https://img.shields.io/badge/Wiki-Docs-blue.svg" style="display: inline-block; vertical-align: middle;"/>
    </a>
</div>

## 📖 Description

JDA-Newtils is a modern series of tools and utilities for use with [JDA](https://github.com/DV8FromTheWorld/JDA) to assist in bot creation.

## 🔬 Installation

You will need to add this project as a dependency (via Maven or Gradle), as well as [JDA](https://github.com/DV8FromTheWorld/JDA).

The minimum java version supported by JDA-Newtils is Java SE 11.

[![](https://jitpack.io/v/SaseQ/JDA-Newtils.svg)](https://jitpack.io/#SaseQ/JDA-Newtils)

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependency>
    <groupId>com.github.SaseQ</groupId>
    <artifactId>JDA-Newtils</artifactId>
    <version>$version</version> <!-- replace $version with the latest version -->
</dependency>
```

### Gradle

```gradle
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		maven { url 'https://jitpack.io' }
	}
}
```
```gradle
dependencies {
    implementation 'com.github.SaseQ:JDA-Newtils:$version' // replace $version with the latest version
}
```

## 🏃‍♂️ Getting Started

We provide a number of [examples]() to introduce you to JDA-Newtils. You can also take a look at our official [Wiki](https://github.com/SaseQ/JDA-Newtils/wiki).

Starting your bot and setup default JDA-Newtils configuration:

```java
public static void main(String[] args) {
    JDA jda = JDABuilder.createDefault("$token") // replace $token your discord bot token
            .build();

    CommandManager manager = new CommandManager();
    manager.setOwnerId("$owner_id"); // replace $owner_id with your user discord id
    manager.setDevGuildId("$dev_guild_id"); // replace $dev_guild_id with your discord server id (remove this line on prod)

    manager.addCommand(new Ping());

    jda.addEventListener(manager);
}
```

Example of how to create a ping SlashCommand which we registered earlier in the main function:

```java
public class Ping extends SlashCommand {

    public Ping() {
        this.name = "ping";
        this.description = "Performs a ping to see the bot's delay";
    }

    @Override
    public void execute(@NonNull SlashCommandInteractionEvent event) {
        event.reply("Ping: ...").queue(m -> 
            m.editOriginal("Pong! " + event.getJDA().getGatewayPing() + "ms").queue()
        );
    }
}
```

A more detailed examples with other JDA-Newtils tools can be found in the [Wiki](https://github.com/SaseQ/JDA-Newtils/wiki).
