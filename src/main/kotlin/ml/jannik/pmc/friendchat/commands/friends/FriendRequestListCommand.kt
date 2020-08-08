package ml.jannik.pmc.friendchat.commands.friends

import ml.jannik.pmc.friendchat.db.Users
import ml.jannik.pmc.friendchat.db.FCUser

import org.bukkit.entity.Player
import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.hover.content.Text

class FriendRequestListCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(sender !is Player) {
      sender.sendMessage("this command is not usable from the console")
      return true
    }

    val list: List<FCUser> = Users.listFriendRequests(sender.uniqueId)

    if(list.isEmpty()) {
      sender.sendMessage("you haven't got any pending friend requests")
      return true
    }

    val message = list.fold(ComponentBuilder("-- you've got friend requests from --")) { acc, user ->
      acc // TODO: determine by rank
        .append("\n" + user.display_name).color(ChatColor.RED).event(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(user.uuid.toString()))).append("").reset()
        .append(" [")
        .append("accept")
          .color(ChatColor.GREEN).bold(true)
          .event(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("click to accept")))
          .event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friendaccept ${user.uuid}"))
          .append("").reset()
        .append(" / ")
        .append("decline")
          .color(ChatColor.RED).bold(true)
          .event(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("click to decline")))
          .event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/frienddecline ${user.uuid}"))
          .append("").reset()
        .append("]")
    }.create()

    sender.sendMessage(*message)

    return true
  }
}