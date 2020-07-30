package ml.jannik.pmc.friendchat.commands.guilds

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class NoGuildChatCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {
    sender.sendMessage("no guild chat: " + args.joinToString { it -> "\'${it}\'"})
    return true
  }
}