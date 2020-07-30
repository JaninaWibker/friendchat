<img align="left" src="./friendchat-logo.svg" width="128px" alt="FriendChat" />

# FriendChat

> **WIP**: This project is in early stages of development

<br />
<br />

A [PaperMC](https://papermc.io/) plugin implementing a social system including guilds, friends, teams, ranks, and more.

The project is meant as a foundation to work upon with other plugins. The whole teams and chat room functionality is
intended to be used by other plugins for creating minigames, having different dimensions or similar.

## Features

- **Friends**: adding / removing friends and being able to easily talk to them. Even across (Waterfall / BungeeCord) servers.
  This does not mean that being friends on one server makes you friends on another. The scope of the friend relation is limited
  to the domain of the server hub. This simply allows having multiple servers (hub, different gamemodes, minigames, ...) and still
  being able to do messaging with other people on the same "group" of servers.
- **Guilds**: create / join / manage guilds. Similar to other features this is partially meant to be used by other plugins but unlike
  teams is not entirely controlled by other plugins. Guilds could be used on scoreboards, have access to shared bases and much more.
  This plugin provides APIs for allowing exactly that.
- **Teams**: teams are for temporary groupings of players. This can mean grouping players by their current dimension, minigames or similar.
  This allows having "throw away guild-like"-*things*. Completely intended to be used by other plugins.
  **Chat rooms**: chat rooms are an automatically managed *thing* accessible to teams, guilds and friends. Guilds have guild chat, teams
  have team chat and friends have so called friend chat. This means that any chat message sent is not being sent to the "main" chat but
  only to the currently active chat room. This allows omitting "/guildmsg <message>" or similar in favor of just "<message>".
  **Ranks**: Similar to most other rank systems in other plugins. Can also be used programmatically by other plugins to grant specific
  privileges.
  **Titles**: Also meant as a programmatically used feature: Grant players specific title for reaching *something*. Similar to titles
  earned in MMORPGs. The player can have multiple titles and can choose which to display.

## Installation

After downloading a release build (which will be available soon :tm:) copy the friend-chat.jar file into the plugin directory
of your papermc server. When building from source the build task should have already copied the jar file to the correct location;
if not then check that `build.gradle` has the correct workspace set. The workspace needs to match the workspace in `start.sh`.

## Configuration

**WIP**: this will follow sometime soon

## Project structure

This project is entirely written in kotlin and uses PostgreSQL as it's database. The database could theoretically be exchanged for
sqlite or something similar with only a few modifications. Full sqlite support might be a future goal.

The project is build using gradle and has the following structure

```
root
 |> design                -- design documents
 |> build                 -- this is excluded using .gitignore
 |> .env                  -- this is excluded using .gitignore -- TODO: should dotenv be used? doesn't papermc have some sort of yaml solution for configs?
 |> start.sh              -- script to start papermc server
 |> build.gradle          -- gradle build task
 |> src/main
    |> kotlin/ml/jannik/pmc/friendchat
    |   |> commands       -- all chat commands
    |   |   |> friends
    |   |   |> guilds
    |   |   |> teams
    |   |   |> other
    |   |> events         -- event handling
    |   |> ...            -- this will probably include a lot more directories in the future
        |> PluginEntry.kt -- plugin entry point
    |> resources
        |> config.yml     -- plugin config file (the config file the user will have to change/create when using the plugin)
        |> plugin.yml     -- plugin information for papermc
```
