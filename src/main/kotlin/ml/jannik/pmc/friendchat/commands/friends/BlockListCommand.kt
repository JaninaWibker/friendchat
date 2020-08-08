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

fun org.bukkit.ChatColor.toBungeeChatColor(): ChatColor {
  return ChatColor.of(this.name)
}

class BlockListCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(sender !is Player) {
      sender.sendMessage("this command is not usable from the console")
      return true
    }

    val list = Users.listBlockedlist(sender.uniqueId)

    val finalMessage = list.fold(ComponentBuilder("-- blocked players --")) { acc, user ->
      acc
        .append("\n")
        .append(user.display_name)
          .event(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(user.uuid.toString())))
          .color(user.rank.color.toBungeeChatColor())
          .append("").reset()
        .append(" [")
        .append("unblock")
          .bold(true).color(ChatColor.RED)
          .event(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("click to unblock")))
          .event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unblock ${user.uuid}")) // TODO: should this maybe use SUGGEST_COMMAND instead?
          .append("").reset()
        .append("]")
    }.create()

    sender.sendMessage(*finalMessage)
    return true
  }
}