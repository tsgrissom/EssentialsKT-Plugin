# EssentialsKT

An addon for EssentialsX written for modern CraftBukkit servers. Replaces and adds many Essentials-like commands.

Developed with Kotlin version 1.9 and modern Java 17

Tested on PaperMC (Java 20) for Minecraft version 1.20.1

## Features
* Modern, powerful EssentialsX addon written in Kotlin
* Designed with EssentialsX permissions in mind for drop-in compatibility with minimal breakage
* Supports any flavor of Bukkit server: Spigot, Paper, etc.
* Commands with intelligent user feedback
* Configurable event messages
* A [plugin API](https://github.com/tsgrissom/EssentialsKT/tree/main/src/main/kotlin/io/github/tsgrissom/pluginapi) powering the plugin features

## Commands
* EssentialsKT imitates the command structure of EssentialsX, including supporting aliases prefixed with `e`
* Permission structure mirrors that of EssentialsX, allowing it to serve as a drop-in replacement with minimal breakage of permission structure
#### General-Purpose (A to Z)
* `/clearchat`
* `/feed`
* `/gamemode`, `/gmc`, `/gms`, etc.
* `/heal`
* `/list`
* `/nickname`
* `/ping`
* `/remove`
* `/renameitem`
* `/suicide`
* `/toggledownfall`
* `/whois` & `/whoami`
#### Time Setting
* Quick-time commands: `/day`, `/midnight`, `/night`, `/noon`, `/sunset`, & `/sunrise`
* In development: `/ntime`
#### Weather Setting
* Quick-weather commands: `/rain`, `/clear`
* In development: `/weather`


## Upcoming Commands
* repair
* kickall
* tpall
* tprequest
* tpaccept
* tpdeny
* tphere
* tprandom
* tpauto
* realname
* compass
* world
* me
* kill
* invsee
* god
* broadcast
* burn
* exp
* 

## Permissions
* Permissions are structured identically between EssentialsX and EssentialsKT to ensure minimal breakage
* See [`plugin.yml`](https://github.com/tsgrissom/EssentialsKT/blob/main/src/main/resources/plugin.yml) until Permissions page on the wiki is built-out