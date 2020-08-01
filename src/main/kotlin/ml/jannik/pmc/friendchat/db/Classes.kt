package ml.jannik.pmc.friendchat.db

import java.util.UUID
import java.util.Date

data class FCUser(val uuid: UUID, val display_name: String, val alt_of: UUID, val rank: String?, val selected_title: String?, val created_date: Date)

data class FCTitle(val key: String, val name: String, val description: String?)

data class FCRank(val key: String, val name: String, val description: String?)

data class FCGuild(val id: UUID, val name: String, val description: String?, val owner: UUID, val created_date: Date)

data class FCTeam(val id: UUID, val name: String?, val room: UUID, val created_date: Date)

data class FCRoom(val id: UUID, val name: String?, val created_date: Date)