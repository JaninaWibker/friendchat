package ml.jannik.pmc.friendchat.commands.friends

import org.bukkit.entity.Player
import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

import ml.jannik.pmc.friendchat.db.Users

class UnblockCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(args.isEmpty()) {
      return false
    }

    if(sender !is Player) {
      sender.sendMessage("this command is not usable from the console")
      return true
    }

    val target = Users.findByDisplayName(args[0]) // TODO: add support for supplying uuids

    if(target === null) {
      sender.sendMessage("player \"${args[0]}\" not found")
      return true
    }

    if(target.uuid == sender.uniqueId) {
      sender.sendMessage("you cannot (un-)block yourself")
      return true
    }

    if(Users.removeFromBlockedlist(sender.uniqueId, target.uuid)) {
      sender.sendMessage("successfully unblocked player \"${target.display_name}\"")
    } else {
      sender.sendMessage("player \"${target.display_name}\" is not blocked")
    }


    return true
  }
}