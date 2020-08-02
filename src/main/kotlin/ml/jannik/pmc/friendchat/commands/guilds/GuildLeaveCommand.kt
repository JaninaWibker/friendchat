package ml.jannik.pmc.friendchat.commands.guilds

import ml.jannik.pmc.friendchat.db.FCGuild
import ml.jannik.pmc.friendchat.db.Guilds

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class GuildLeaveCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(args.isEmpty()) return false

    if(sender !is Player) {
      sender.sendMessage("this command cannot be executed from the console")
      return true
    }

    val guildName = args[0]

    val guild: FCGuild? = Guilds.findByName(guildName)

    var canSee = true

    if(guild !== null) {
      if(guild.owner == sender.uniqueId) {
        val members = Guilds.listMembersById(guild.id)
          .filter { it.user.uuid != sender.uniqueId }
          .sortedBy { it.joined_date }

        if(Guilds.leaveGuild(guild.id, sender.uniqueId)) {
          if(members.isEmpty()) { 
            // delete the guild as nobody is in the guild anymore
            Guilds.deleteGuild(guild.id)
            sender.sendMessage("left guild \"${guild.name}\" successfully and deleted it since no members were left")
          } else {
            // choose new owner from top of member list
            Guilds.transferOwnershipGuild(guild.id, members[0].user.uuid)
            sender.sendMessage("left guild \"${guild.name}\" successfully and transfered ownership to \"${members[0].user.display_name}\"")
          }
        } else {
          canSee = false // this should never happen, if it does this means some database state was inconsistent
        }
      } else {
        if(Guilds.leaveGuild(guild.id, sender.uniqueId)) {
          sender.sendMessage("left guild \"${guild.name}\" successfully")
        } else {
          canSee = false
        }
      }
    } else {
      canSee = false
    }

    if(!canSee) {
      sender.sendMessage("guild \"$guildName\" does not exist or you are not a part of it")
    }
    
    return true
  }
}