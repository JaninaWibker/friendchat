package ml.jannik.pmc.friendchat.commands.friends

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class FriendAcceptCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {
    sender.sendMessage("friend accept: " + args.joinToString { it -> "\'${it}\'"})
    return true
  }
}