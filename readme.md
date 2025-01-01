<img align="left" src="./friendchat-logo.svg" width="128px" alt="FriendChat" />

# FriendChat

> **WIP**: This project is in very **early** stages of development

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

Both building from source and just using a release build will require a postgres database. Since this varies a lot between distros & OS'es
it is not covered here. You will most likely want to create a new user for the friendchat database:

```sh
su - postgres # do this as root as you will otherwise have to enter a password you don't know
create --interactive --pwprompt
# enter username; friendchat is fitting
# enter password
# enter password again
# enter n (you don't want your new user to be a superuser)
# probably also n (you'll create the database, the new user doesn't need to have this permission) 
# enter n (the user does not need to add new users)
```

It might be useful to create yourself a user as well, this time with superuser privileges. This way you can add databases without having to
use root

Now you need to create the database

```sh
# as root or your user which you gave superuser rights to
createdb -O friendchat friendchat
# Usage: createdb -O <username> <db name>
```

Incase you want to delete the database later on just do

```sh
dropdb friendchat
# Usage: dropdb <db name>
```

The same works for deleting users:

```sh
dropuser friendchat
# Usage: dropuser <username>
```

### Building from Source

First of all getting a papermc server up and running.

```sh
./start.sh # downloads papermc and starts the server; use this to start the development server (it'll only download papermc once)
```

You may need to modify the line specifying the workspace. This is where the papermc server will live. After running the script once
it will probably tell you that you need to agree to mojangs EULA. Do this by changing the `eula=false` line to `eula=true` in the
eula.txt file *after reading the EULA of course*.

After that the server should work just fine.

The project uses gradle which makes it really easy to build the project. You may need to adjust the workspace in the `build.gradle` file
as well.

```sh
gradle build # builds the project and moves the .jar file to the plugins folder
```

### Using a release build

After downloading a release build (which will be available soon :tm:) copy the friend-chat.jar file into the plugin directory
of your papermc server and follow the steps outlined in *Configuration*.

## Configuration

**WIP**: this will follow sometime soon

## Project structure

This project is entirely written in kotlin, uses gradle for building and PostgreSQL as it's database. The database could theoretically
be exchanged for sqlite or something similar with only a few modifications. Full sqlite support might be a future goal.

The project is build using gradle and has the following structure

```
root
 |> design                -- design documents
 |> build                 -- this is excluded using .gitignore
 |> start.sh              -- script to start papermc server
 |> build.gradle          -- gradle build task
 |> src/main
    |> kotlin/lol/janina/pmc/friendchat
    |   |> commands       -- all chat commands
    |   |   |> friends
    |   |   |> guilds
    |   |   |> teams
    |   |   |> other
    |   |> events         -- event handling
    |   |> db             -- database related stuff (support for a different database would be implemented here)
    |   |> ...            -- this will probably include a lot more directories in the future
        |> PluginEntry.kt -- plugin entry point
    |> resources
        |> config.yml     -- plugin config file (the config file the user will have to change/create when using the plugin)
        |> plugin.yml     -- plugin information for papermc
```
