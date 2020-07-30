package ml.jannik.pmc.friendchat.events.player

import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.ChatColor

class LeaveEvent : Listener {

  @EventHandler fun onPlayerJoin(e: PlayerQuitEvent) {
    e.quitMessage = ChatColor.BLUE.toString() + e.player.displayName + ChatColor.WHITE.toString() + " left the game"
  }
}