package ml.jannik.pmc.friendchat.events.player

import ml.jannik.pmc.friendchat.PluginEntry
import ml.jannik.pmc.friendchat.db._FCUser
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.ChatColor

import ml.jannik.pmc.friendchat.db.Users
import org.bukkit.entity.Player
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class JoinEvent : Listener {

  @EventHandler fun onPlayerJoin(e: PlayerJoinEvent) {

    val p: Player = e.player

    e.joinMessage = (if(Users.exists(p.uniqueId)) "+" else "-") + ChatColor.BLUE.toString() + p.displayName + ChatColor.WHITE.toString() + " joined the game"

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