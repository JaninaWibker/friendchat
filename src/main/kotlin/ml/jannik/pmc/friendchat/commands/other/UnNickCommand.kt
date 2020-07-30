package ml.jannik.pmc.friendchat.commands.other

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class UnNickCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {
    sender.sendMessage("unnick: " + args.joinToString { it -> "\'${it}\'"})
    return true
  }
}