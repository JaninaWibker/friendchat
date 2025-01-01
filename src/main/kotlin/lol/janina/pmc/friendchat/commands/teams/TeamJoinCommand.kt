package lol.janina.pmc.friendchat.commands.teams

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class TeamJoinCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {
    sender.sendMessage("team join: " + args.joinToString { it -> "\'${it}\'"})
    return true
  }
}