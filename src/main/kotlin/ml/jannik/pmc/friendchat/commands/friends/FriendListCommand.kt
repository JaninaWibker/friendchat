package ml.jannik.pmc.friendchat.commands.friends

import ml.jannik.pmc.friendchat.db.Users
import ml.jannik.pmc.friendchat.db.FCUser
import org.bukkit.entity.Player

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class FriendListCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(sender !is Player) {
      sender.sendMessage("this command is not usable from the console")
      return true
    }

    val list: List<FCUser> = Users.listFriendlist(sender.uniqueId)

    sender.sendMessage("friendlist:\n" + list.map { "- ${it.display_name}" }.joinToString("\n"))

    return true
  }
}