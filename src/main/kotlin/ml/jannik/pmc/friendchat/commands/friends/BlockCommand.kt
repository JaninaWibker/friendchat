package ml.jannik.pmc.friendchat.commands.friends

import java.util.UUID

import org.bukkit.entity.Player
import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

import ml.jannik.pmc.friendchat.db.Users

class BlockCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    val uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"

    if(args.isEmpty()) {
      return false
    }

    if(sender !is Player) {
      sender.sendMessage("this command is not usable from the console")
      return true
    }

    val target =
      if(args[0].matches(Regex(uuidRegex)))
        Users.findByUUID(UUID.fromString(args[0]))
      else
        Users.findByDisplayName(args[0])

    if(target === null) {
      sender.sendMessage("player \"${args[0]}\" not found")
      return true
    }

    if(target.uuid == sender.uniqueId) {
      sender.sendMessage("you cannot block yourself")
      return true
    }

    if(Users.addToBlockedlist(sender.uniqueId, target.uuid)) {
      Users.removeFromFriendlist(sender.uniqueId, target.uuid) // removing as friend
      Users.removeFromFriendrequests(sender.uniqueId, target.uuid) // removing friend requests sent from command sender to target
      Users.removeFromFriendrequests(target.uuid, sender.uniqueId) // removing friend requests sent from target to command sender
      sender.sendMessage("successfully blocked player \"${target.display_name}\"")
    } else {
      sender.sendMessage("player \"${target.display_name}\" is already blocked")
    }

    return true
  }
}