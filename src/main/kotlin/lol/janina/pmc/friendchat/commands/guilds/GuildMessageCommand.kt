package lol.janina.pmc.friendchat.commands.guilds

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class GuildMessageCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {
    sender.sendMessage("guild message: " + args.joinToString { it -> "\'${it}\'"})
    return true
  }
}