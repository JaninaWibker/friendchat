package lol.janina.pmc.friendchat.db

import java.util.UUID
import java.util.Date

import org.bukkit.ChatColor

// There are sometimes multiple variants of the same database table
// This has the reason that some values (mostly ids and dates) are autogenerated by the database
// when trying to create a new database entry these data classes should be used but some values
// are not known at that time. Instead of just making all the possibly unknown values nullable,
// which would cause issues when later using the same data class when the values are 100% known
// since the information is coming from the database, another data class is created which does
// not require the additional information to be present
// These other data classes are marked with an underscore in the beginning and are only present
// when they are really required (FCTitle and FCRank use a simple String as a private key which
// is user-specified meaning the key is known at creation time -> no "_"-data classes required)

data class  FCUser(val uuid: UUID, val display_name: String, val alt_of: UUID, val rank: FCRank,  val selected_title: FCTitle?, val created_date: Date)
data class _FCUser(val uuid: UUID, val display_name: String, val alt_of: UUID, val rank: String?, val selected_title: String?, val created_date: Date? = null)

data class FCTitle(val key: String, val name: String, val description: String?)

data class FCRank(val key: String, val name: String, val description: String?, val color: ChatColor)

data class  FCGuild(val id: UUID, val name: String, val description: String?, val owner: UUID, val room: UUID, val created_date: Date)
data class _FCGuild(              val name: String, val description: String?, val owner: UUID,                 val created_date: Date? = null)

data class  FCTeam(val id: UUID, val name: String?, val room: UUID, val created_date: Date)
data class _FCTeam(              val name: String?, val created_date: Date? = null)

data class  FCRoom(val id: UUID, val name: String?, val is_default_room: Boolean=false, val created_date: Date)
data class _FCRoom(              val name: String?)
