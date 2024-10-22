# EssentialsKT Plugin

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
* Permission structure mirrors that of EssentialsX, allowing it to serve as a drop-in addon with minimal breakage of permission structure
#### General-Purpose (A to Z)
* `/clearchat`
* `/damage`
* `/esskt`
* `/gamemode`, `/gmc`, `/gms`, etc.
* `/ipaddress`
* `/list`
* `/remove`
* `/renameitem`
* `/setfoodlevel`
* `/sethealth`
* `/time`
* `/toggledownfall`
* `/uniqueid`
* `/weather`
* `/whois` & `/whoami`
* `/worlds`
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

## Permissions
* Permissions are structured identically between EssentialsX and EssentialsKT to ensure minimal breakage
* See [`plugin.yml`](https://github.com/tsgrissom/EssentialsKT/blob/main/src/main/resources/plugin.yml) until Permissions page on the wiki is built-out

## Source Code Notes
* All files are organized while striving to meet best practices.
* For very large files, it is recommended to enable a custom TODO marker in your IntelliJ IDE as such:
  1. Go to `Settings>Editor>TODO`
  2. Create a new TODO marker with the following settings (the regex & case-insensitive are the important parts)
![TODO Marker Settings](https://i.ibb.co/0fDYPpH/SCR-20231010-nhmn.png)
* This will allow you to highlight the Xcode-style markers placed throughout larger files to highlight and break-up larger sections of code.
