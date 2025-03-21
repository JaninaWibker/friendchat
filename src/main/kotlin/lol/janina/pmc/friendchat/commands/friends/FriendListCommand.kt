package lol.janina.pmc.friendchat.commands.friends

import lol.janina.pmc.friendchat.db.Users
import lol.janina.pmc.friendchat.db.FCUser

import org.bukkit.entity.Player
import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.hover.content.Text

class FriendListCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(sender !is Player) {
      sender.sendMessage("this command is not usable from the console")
      return true
    }

    val list: List<FCUser> = Users.listFriendlist(sender.uniqueId)

    if(list.isEmpty()) {
      sender.sendMessage("you haven't added any friends yet")
      return true
    }

    val message = list.fold(ComponentBuilder("-- friend list (date/desc) --")) { acc, user ->
      acc
        .append("\n" + user.display_name)
        .color(ChatColor.RED) // TODO: determine by rank
        .event(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(user.uuid.toString())))
        .append("").reset()
    }.create()

    sender.sendMessage(*message)

    return true
  }
}