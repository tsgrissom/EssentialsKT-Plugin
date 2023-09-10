# EssentialsKT

A test plugin for CraftBukkit servers written in the Kotlin programming language

Tested on PaperMC with Minecraft version 1.20.1

## Features
* Lightweight Essentials replacer written in Kotlin
* Supports any flavor of Bukkit server: Spigot, Paper, etc.
* Commands with intelligent user feedback
* Configurable messages for join, quit, and chat events
* A [plugin API](https://github.com/tsgrissom/EssentialsKT/tree/main/src/main/kotlin/io/github/tsgrissom/pluginapi) powering the plugin features

## Commands
* EssentialsKT imitates the command structure of EssentialsX, including supporting aliases prefixed with `e`
* Permission structure mirrors that of EssentialsX, allowing it to serve as a drop-in replacement with minimal breakage of permission structure
#### General-Purpose (A to Z)
* `/gamemode`, `/gmc`, `/gms`, etc.
* `/heal`
* `/ping`
* `/suicide`
* `/whois` & `/whoami`
#### Time Setting
* Quick-time commands: `/day`, `/midnight`, `/night`, `/noon`, `/sunset`, & `/sunrise`

## Permissions
* Permissions are structured differently between EssentialsX and EssentialsKT
* See [`plugin.yml`](https://github.com/tsgrissom/EssentialsKT/blob/main/src/main/resources/plugin.yml) until Permissions page on the wiki is built-out