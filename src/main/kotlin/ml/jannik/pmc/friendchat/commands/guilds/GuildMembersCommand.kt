package ml.jannik.pmc.friendchat.commands.guilds

import ml.jannik.pmc.friendchat.db.Guilds
import ml.jannik.pmc.friendchat.db.FCUser
import ml.jannik.pmc.friendchat.db.FCGuild
import ml.jannik.pmc.friendchat.db.FCUserWithJoinDate

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GuildMembersCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(args.isEmpty()) return false

    val guildName = args[0]

    var canSee = true // this is false when the guild doesn't exist or the player has no permission to view the members

    val guild: FCGuild? = Guilds.findByName(guildName)

    if(guild !== null) {
      val guildMembers: List<FCUserWithJoinDate> = Guilds.listMembersByName(guildName)
      
      if(sender is Player && guildMembers.find { it.user.uuid == sender.uniqueId } === null) { // TODO: consider permissions?
        canSee = false
      }

      if(canSee) {
        sender.sendMessage("guild members:\n" + guildMembers.joinToString(separator = "\n") {
          "- ${it.user.display_name} (joined ${it.joined_date}; ${it.user.uuid}${if(guild.owner == it.user.uuid) "; owner" else ""})"
        })
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