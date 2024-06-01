# 🚚 MobChipLite
> Minecraft Entity AI and Bosses Library for 1.17 and above

## Background
<details>
    <summary>Click to Expand</summary>
    
MobChipLite is an all-in-one Entity AI and Bosses Library for Minecraft 1.17 and above. It allows you to easily implement Minecraft's native entity
AI into your own plugins for simple use.

MobChipLite is a fork of the original MobChip library, which was created by gmitch215/GamerCoder215. MobChipLite is a continuation of the original library, with some features and versions removed, but the ultimate goal of updating it to work with the latest versions of Minecraft.
</details>

## ❓ Why?

- **Simple**: MobChip has documentation, API usage, and other utilities to help ease the experience of working with Entity AI.
- **Flexible**: MobChip uses Reflection and Abstraction to help create flexibility, in order to ensure modern functionality on older versions. We also provide an easy-to-read API and JavaDocs to access important fields and methods. 
- **Compatibility**: MobChip uses Abstraction to create compatibility on multiple versions of Minecraft. We also provide a version checker to ensure that your plugin is running on a compatible version.
- **Transparent**: MobChip is completely open source.

## 🐘 Features

- Bosses Library 
- Native Entity AI Wrappers
  - Pathfinder Goals
  - Behaviors
  - Memories
  - Villager Gossip
  - Ender Dragon Phases
  - Tick Schedules
  - Sensors
- Native Entity Navigation & Controllers 
- Native Entity Animations
- Native Entity Combat Tracking
- Entity NBT Editor
- Custom Entity Attributes


## 📥 Installation

<details>
    <summary>Maven</summary>

```xml
<project>
    
    <!-- Import CodeMC Repo -->
    
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>com.github.datatags.MobChipLite</groupId>
            <artifactId>mobchip-bukkit</artifactId>
            <version>[VERSION]</version>

            <!-- Use something like 1.7.0-SNAPSHOT for a stable release -->
        </dependency>
    </dependencies>
    
</project>
```
</details>

<details>
    <summary>Gradle (Groovy)</summary>

```gradle
repositories {
    maven { url 'https://repo.codemc.io/repository/maven-snapshots/' }
}

dependencies {
    // Use something like 1.9.1-SNAPSHOT for a stable release
    implementation 'me.gamercoder215:mobchip-bukkit:[VERSION]'
}
```
</details>

<details>
    <summary>Gradle (Kotlin DSL)</summary>

```kotlin
repositories {
    maven(url = "https://jitpack.io")
}

dependencies {
    // Use something like 1.9.1-SNAPSHOT for a stable release
    implementation('com.github.datatags.MobChipLite:mobchip-bukkit:[VERSION]')
}
```
</details>
