package lol.janina.pmc.friendchat.commands.guilds

import lol.janina.pmc.friendchat.PluginEntry
import lol.janina.pmc.friendchat.db.Guilds
import lol.janina.pmc.friendchat.db.FCUserWithJoinDate
import lol.janina.pmc.friendchat.db.FCGuild

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class GuildDeleteCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(args.isEmpty()) {
      return false
    }

    val guild: FCGuild? = Guilds.findByName(args[0])

    if(guild === null) {
      sender.sendMessage("guild not found or not a member")
      return true
    }

    if(sender !is Player) {
      if(Guilds.deleteGuild(guild.id)) {
        sender.sendMessage("successfully deleted guild \"${guild.name}\"")
        return true
      } else {
        sender.sendMessage("deleting guild \"${guild.name}\" failed")
        return true
      }
    }

    val members: List<FCUserWithJoinDate> = Guilds.listMembersById(guild.id)

    if(members.find { it.user.uuid == sender.uniqueId } === null) {
      sender.sendMessage("guild not found or not a member")
      return true
    }

    if(guild.owner != sender.uniqueId) {
      sender.sendMessage("not the owner of the guild")
      return true
    }

    if(Guilds.deleteGuild(guild.id)) {
      sender.sendMessage("successfully deleted guild \"${guild.name}\"")
      return true
    } else {
      sender.sendMessage("deleting guild \"${guild.name}\" failed")
      return true
    }
  }
}