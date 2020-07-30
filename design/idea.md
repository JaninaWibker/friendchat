# FriendChat

The idea behind FriendChat (or friend-chat) is to be able to:

- add other people as friends
- create teams / guilds
  - teams and guilds are independent from each other. Guilds are things players can create, teams are things other plugins can create for minigames or similar
- chat rooms for teams, guilds, dimensions, servers
  - messages are only sent to people in the current chat room, the current chat room is indicated by chat messages
- easy private messaging (using /r, ...)
- contacting admins / mods easily via support system
- add other accounts as alts, this means that everything that the main account can do the alt can also do; this basically means that the accounts are equivilant
  when it comes to certain things. This doesn't mean that the friendlists are shared automatically; other people should not know that the alt account is associated
  with the main account
- nicking (changing the nickname in order to "go undercover")
- potentially cross server functionality (for servers connected by BungeeCord mostly; but could be used for more)
- customizable join / leave messages
- customizable chat messages (nickname colors, ...)
- database as backend (use transactions for atomicity)
- optional logging of player logins / logouts (don't really like logging as this doesn't agree with my personal opinion on privacy)
- chat messages are private; admins cannot inspect them as such a thing would not play well with my personal opinion on privacy 

Other ideas which might be split off into different plugins
- permission system which is useable by other plugins: this would also hook into guilds / alt accounts and such; alt accounts share permissions
  for all things **NOT** involved in chatting, ... (like changing nickname, ...)
