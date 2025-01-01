package lol.janina.pmc.friendchat.commands.friends

import lol.janina.pmc.friendchat.db.Users
import lol.janina.pmc.friendchat.db.FCUser
import org.bukkit.entity.Player

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class FriendRemoveCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(sender !is Player) {
      sender.sendMessage("this command is not usable from the console")
      return true
    }

    if(args.isEmpty()) {
      return false
    }

    val user: FCUser? = Users.findByDisplayName(args[0])

    if(user?.uuid == sender.uniqueId) {
      sender.sendMessage("you cannot unfriend yourself")
      return true
    }

    if(user !== null && Users.removeFromFriendlist(sender.uniqueId, user.uuid)) {
      sender.sendMessage("successfully removed \"${user.display_name}\" from your friendlist")
    } else {
      sender.sendMessage("this user does not exist or is not on your friendlist")
    }

    return true
  }
}