package ml.jannik.pmc.friendchat.commands.guilds

import ml.jannik.pmc.friendchat.PluginEntry
import ml.jannik.pmc.friendchat.db.Guilds
import ml.jannik.pmc.friendchat.db._FCGuild

import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class GuildCreateCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {
    // TODO: interpret double quoted arguments (even with spaces) as a single argument
    if(args.isEmpty()) return false

    if(sender !is Player) {
      sender.sendMessage(ChatColor.RED.toString() + "can only create guilds as a player as otherwise no owner could be specified")
      return true
    }

    val guildName = args[0]
    val description = args.sliceArray(1 until args.size).joinToString(" ")

    if(Guilds.exists(guildName)) {
      sender.sendMessage(ChatColor.RED.toString() + "guild with this name exists already")
      return true
    }

    val uuid: UUID = Guilds.create(_FCGuild(
      name = guildName,
      description = description,
      owner = sender.uniqueId,
      created_date = null
    ))

    sender.sendMessage("guild \"${ChatColor.GOLD}$guildName${ChatColor.RESET}\" created")

    Logger.getLogger(PluginEntry::class.java.name).log(Level.INFO, "created database entry ($uuid)")


    return true
  }
}