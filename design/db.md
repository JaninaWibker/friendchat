---
prefix: FC
connprefix: CONN
---

User {
  *uuid: UUID
  display_name: string // if null just use default display name
  alt_of: User
  rank: Rank
  #friends: [User](*) #Friends.users[this] => Friends.users[!this]
  #alts: [User](*) #User.alt_of = this && User != this => User
  #teams: [Team](*) #TeamUserConnection.user = this => TeamUserConnection.team // are teams saved in the db or just in-memory?
  #guilds: [Guild](*) #GuildUserConnection.user = this => GuildUserConnection.guild
  #permissions: [Permission](*) // maybe use pre-existing features instead
  #rooms: [Room](*) #RoomUserConnection.user = this => RoomUserConnection.room
  #titles: [Title](*) #UserTitleConnection.user = this => UserTitleConnection.title
  #friendrequests_outgoing: [Friendrequest](*) #Friendrequest.from = this => Friendrequest
  #friendrequests_incoming: [Friendrequest](*) #Friendrequest.to   = this => Friendrequest
  #guildinvites_incoming: [Guildinvite](*) #Guildinvite.user = this => Guildinvite
  #blocked: [User](*) #UserBlockConnection.blockee = this => UserBlockConnection.blocked
  created_date: Date
}

Friendrequest {
  *from: User
  *to:   User
  created_date: Date
}

Guildinvite {
  user: User
  guild: Guild
  created_date: date
}

Team {
  *id: UUID
  name: string // can be auto-generated
  room: Room
  #members: [User](*) #TeamUserConnection.team = this => TeamUserConnection.user
  created_date: Date
}

Guild {
  *id: UUID
  name: string
  description: string*
  room: Room
  #members: [User](*) #GuildUserConnection.team = this => GuildUserConnection.user
  owner: User
  created_date: Date
}

Rank {
  *id: UUID
  name: string
  description: string*
  #permissions: [Permission](*) // maybe use pre-existing features instead and use a hierarchy of ranks
}

Room {
  *id: UUID,
  name: string* // can be auto-generated
  #members: [User](*) #RoomUserConnection.room = this => RoomUserConnection.user
  created_date: Date
}

Title {
  *id: UUID
  name: string
  description: string
}

---- Connections ----

UserBlockConnection {
  blocked: User
  blockee: User
  created_date: date
} -> FC_CONN_Blocked

UserFriendConnection {
  *users: [User](2) %ordered // this allows keeping track of who made the friendrequest even after it is deleted
  *created_date: date
} -> FC_CONN_Friends

TeamUserConnection(team: Team, user: User) {
  created_date: date
}

GuildUserConnection(guild: Guild, user: User) {
  created_date: date
}

RoomUserConnection(room: Room, user: User) {
  created_date: date
}

UserTitleConnection(user: User, title: Title) {
  created_date: date
}
