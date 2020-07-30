# Commands

A important feature is that for players to be used in commands they must have previously logged
in and thereby have a User database record. This is on purpose, don't want people to log in for
the first time and be bombarded with friend requests, ...

## Friends

friendrequest <user/uuid>: sends a friendrequest to the specified user OR if this user
already sent a friendrequest accepts that one.
friendaccept <user/uuid>: accept a friendrequest
frienddeny <user/uuid>: deny a friendrequest
frienddecline <user/uuid>: alias of frienddeny <user/uuid>
block <user/uuid>: block a user
unblock <user/uuid>: unblock a user
friendlist: list friends (and alts?)
friends: alias of friendlist

msg <player/uuid> <message>: message specified player with given message (not quotes required
for message, everything after the player counts as the message)
message <player/uuid> <message>: alias of msg <player/uuid> <message>

r <message>: reply to player which last sent a message using specified message
reply <message>: alias of r <message>

friendchat <user/uuid>: activate friend chat with specified player
friendchat: turn friendchat off
nofriendchat: turn friendchat off


## Guilds

guildinvite <user/uuid>: invite a player to the current guild
guildinvite <guild> <user/uuid>: invite a player to the specified guild

guildaccept: accept the last guild invite received
guildaccept <guild>: accept the guild invite from the specified guild
guilddeny: deny the last guild invite received
guilddeny <guild>: decline the guild invite from the specified guild
guilddecline: alias of guilddeny
guilddecline <guild>: alias of guilddeny <guild>

guildcreate <name>: create a guild with the given name
guildcreate <name> <description (in quotes)>: create a guild with the given name and description
guildtransfer <guild> <user/uuid>: transfer ownership of specified guild to specified user
guildmodify set description <description (in quotes)>

guildkick <guild> <user/uuid>: remove the specified user from the specified guild

guilds, guildlist: list players guilds

guildmembers: list members of current guild

guildmessage <message>, guildmsg <message>: send a message to the guild chat of the currently active guild
guildmessage <guild> <message>, guildmsg <guild> <message>: send a message to the guild chat of the specified guild

guildchat: activate guild chat for currently active guild or toggle guild chat off
guildchat <guild>: activate guild chat for specified guild
noguildchat: turn off guild chat

guildprimary <guild>: set currently active guild

## Teams

Team commands are reserved for people with special permission. Adding players to a team
does not require the player to accept. This is done automatically as teams are ment to be
temporary guild-like "things" for minigames, ... Therefore players need to be auto-added
under certain conditions. This can either be done by other plugins or using commands with
special permissions.

teamcreate: create a team without a name (will return id of team)
teamcreate <name>: create a team with the given name (will return id of team). Name does not
have to be unique, can be something like "blue team"
teamjoin: <team-id> <player/uuid>: add specified player to specified team
teamjoincode <team-id>: get a "join code" for the specified team

teamjoin: <join-code>: join a team using the specified join code (this does not require special permissions)

teamleave <team-id> <player/uuid>: remove the specified player from the specified team


## Other

nick <display-name>: set nickname to specified display name
unnick: reset display name
