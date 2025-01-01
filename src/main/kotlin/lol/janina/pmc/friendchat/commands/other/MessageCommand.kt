package lol.janina.pmc.friendchat.commands.other

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class MessageCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {
    sender.sendMessage("msg: " + args.joinToString { it -> "\'${it}\'"})
    return true
  }
}