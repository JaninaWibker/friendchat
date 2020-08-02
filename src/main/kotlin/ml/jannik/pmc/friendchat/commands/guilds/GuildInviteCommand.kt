package ml.jannik.pmc.friendchat.commands.guilds

import ml.jannik.pmc.friendchat.db.FCGuild
import ml.jannik.pmc.friendchat.db.FCUser
import ml.jannik.pmc.friendchat.db.Guilds
import ml.jannik.pmc.friendchat.db.Users

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GuildInviteCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(args.size != 2) return false

    val guildName = args[0]
    val playerName = args[1]

    val guild: FCGuild? = Guilds.findByName(guildName)

    if(guild === null) {
      sender.sendMessage("guild \"$guildName\" does not exist or you are not a part of it")
      return true
    }

    val player: FCUser? = Users.findByDisplayName(playerName)

    if(player === null) {
      sender.sendMessage("player \"$playerName\" does not exist")
      return true
    }

    val members = Guilds.listMembersById(guild.id)

    if(sender is Player && members.find { it.user.uuid == sender.uniqueId } === null) {
      sender.sendMessage("guild \"$guildName\" does not exist or you are not a part of it")
      return true
    }

    if(sender is Player && player.uuid == sender.uniqueId) {
      sender.sendMessage("cannot invite yourself")
      return true
    }

    if(members.find { it.user.uuid == player.uuid } !== null) {
      sender.sendMessage("player \"${player.display_name}\" is already in the guild")
      return true
    }

    if(Guilds.isInvitedGuild(guild.id, player.uuid)) {
      sender.sendMessage("player \"${player.display_name}\" is already invited to the guild")
    }

    // **at this point**:
    // - sender is either member of the guild or console
    // - player exists
    // - player is not in the guild
    // - guild exists

    // Guilds.inviteGuild(guild.id, player.uuid)

    // TODO: somehow notify the player if he is "online" that he got an invite, even across bungeecord / waterfall.
    // TODO: use some kind of message channel to notify other bungeecord / waterfall servers if the player is not
    // TODO: on the "current" server. Also maybe emulate sending such a message to the own server just to not have
    // TODO: to duplicate code; maybe there is another solution to this that is better; will have to find out.

    /* TODO: remove later; just for testing */ Guilds.joinGuild(guild.id, player.uuid)

    sender.sendMessage(guild.toString())

    return true
  }
}