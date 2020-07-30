package ml.jannik.pmc.friendchat.events.player

import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.ChatColor

class JoinEvent : Listener {

  @EventHandler fun onPlayerJoin(e: PlayerJoinEvent) {
    e.joinMessage = ChatColor.BLUE.toString() + e.player.displayName + ChatColor.WHITE.toString() + " joined the game"
  }
}