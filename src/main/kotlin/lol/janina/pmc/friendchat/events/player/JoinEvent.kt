package lol.janina.pmc.friendchat.events.player

import lol.janina.pmc.friendchat.PluginEntry
import lol.janina.pmc.friendchat.db._FCUser
import lol.janina.pmc.friendchat.db.FCUser
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.ChatColor

import lol.janina.pmc.friendchat.db.Users
import org.bukkit.entity.Player
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class JoinEvent : Listener {

  @EventHandler fun onPlayerJoin(e: PlayerJoinEvent) {

    val p: Player = e.player

    e.joinMessage = (if(Users.exists(p.uniqueId)) "+" else "-") + ChatColor.BLUE.toString() + p.displayName + ChatColor.WHITE.toString() + " joined the game"

    val user: FCUser? = Users.findByUUID(p.uniqueId)

    if(user?.display_name != p.displayName) {
      Logger.getLogger(PluginEntry::class.java.name).log(Level.INFO, "name change detected")
      Users.updateDisplayName(_FCUser(
        uuid = p.uniqueId,
        display_name = p.displayName,
        alt_of = p.uniqueId,
        rank = null, // not used by update command
        selected_title = null // not used by update command
      ))
      // TODO: improve this message
      p.sendMessage("hey, it seems you changed your display name. Just letting you know that everything went alright while migrating your display name")
    }

    if(!Users.exists(p.uniqueId)) {
      Users.create(_FCUser(
        uuid = p.uniqueId,
        display_name = p.displayName,
        alt_of = p.uniqueId,
        rank = null,          // gets auto-filled by sql DEFAULT value
        selected_title = null // gets auto-filled by sql DEFAULT value
      ))
      Logger.getLogger(PluginEntry::class.java.name).log(Level.INFO, "created database entry")
    }
  }
}