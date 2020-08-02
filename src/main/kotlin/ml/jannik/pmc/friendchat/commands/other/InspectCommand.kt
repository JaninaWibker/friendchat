package ml.jannik.pmc.friendchat.commands.other

import ml.jannik.pmc.friendchat.db.FCUser
import ml.jannik.pmc.friendchat.db.FCGuild
import ml.jannik.pmc.friendchat.db.FCTeam
import ml.jannik.pmc.friendchat.db.FCRoom
import ml.jannik.pmc.friendchat.db.FCRank
import ml.jannik.pmc.friendchat.db.FCTitle
import ml.jannik.pmc.friendchat.db.Users
import ml.jannik.pmc.friendchat.db.Guilds
import ml.jannik.pmc.friendchat.db.Teams
import ml.jannik.pmc.friendchat.db.Ranks
import ml.jannik.pmc.friendchat.db.Titles
import ml.jannik.pmc.friendchat.db.Rooms
import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.UUID;

fun jsonColors(color: Boolean=true): Triple<String, String, String> =
  if(color)
    Triple("§9", "§6", "§7")
  else
    Triple("", "", "")

class InspectCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(args.size == 1) return false

    val uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"

    val msg: String? = when(args[0]) {
      "player" -> if(args[1].matches(Regex(uuidRegex))) this.inspectPlayer(UUID.fromString(args[1]))
                  else this.inspectPlayer(args[1])
      "guild"  -> if(args[1].matches(Regex(uuidRegex))) this.inspectGuild(UUID.fromString(args[1]))
                  else this.inspectGuild(args[1])
      "team"   -> if(args[1].matches(Regex(uuidRegex))) this.inspectTeam(UUID.fromString(args[1]))
                  else null
      "room"   -> if(args[1].matches(Regex(uuidRegex))) this.inspectRoom(UUID.fromString(args[1]))
                  else null
      "rank"   -> this.inspectRank(args[1])
      "title"  -> this.inspectTitle(args[1])
      else     -> null
    }

    sender.sendMessage(if(msg === null) "not found" else msg)
    return msg !== null
  }

  private fun inspectPlayer(displayName: String, color: Boolean=true): String {
    return this.inspectPlayer(Users.findByDisplayName(displayName), color)
  }

  private fun inspectPlayer(uuid: UUID, color: Boolean=true): String {
    return this.inspectPlayer(Users.findByUUID(uuid), color)
  }

  private fun inspectPlayer(player: FCUser?, color: Boolean =true): String {

    val (blue, gold, grey) = jsonColors(color)
    
    return if(player === null)
      "player not found"
    else
      """
        ${grey}{
          "${blue}display_name${grey}": "${gold}${player.display_name}${grey}",
          "${blue}uuid${grey}": "${gold}${player.uuid}${grey}",
          "${blue}alt_of${grey}": "${gold}${if(player.alt_of == player.uuid) "self" else player.alt_of.toString()}${grey}",
          "${blue}rank${grey}": ${player.rank},
          "${blue}title${grey}": ${player.selected_title},
          "${blue}created_date${grey}": "${gold}${player.created_date}${grey}"
        }
      """.trimIndent()
  }

  private fun inspectGuild(name: String, color: Boolean=true): String {
    return this.inspectGuild(Guilds.findByName(name), color)
  }

  private fun inspectGuild(uuid: UUID, color: Boolean=true): String {
    return this.inspectGuild(Guilds.findById(uuid), color)
  }

  private fun inspectGuild(guild: FCGuild?, color: Boolean=true): String {

    val (blue, gold, grey) = jsonColors(color)

    return if(guild === null)
      "guild not found"
    else
      """
        ${grey}{
          "${blue}name${grey}": "${gold}${guild.name}${grey}",
          "${blue}id${grey}": "${gold}${guild.id}${grey}",
          "${blue}description${grey}": "${gold}${guild.description}${grey}",
          "${blue}owner${grey}": "${gold}${guild.owner}${grey}",
          "${blue}room${grey}": "${gold}${guild.room}${grey}",
          "${blue}created_date${grey}": "${gold}${guild.created_date}${grey}"
        }
      """.trimIndent()
  }

  private fun inspectTeam(uuid: UUID, color: Boolean=true): String {
    return this.inspectTeam(Teams.findById(uuid), color)
  }

  private fun inspectTeam(team: FCTeam?, color: Boolean=true): String {

    val (blue, gold, grey) = jsonColors(color)

    return if(team === null)
      "team not found"
    else
      """
        ${grey}{
          "${blue}name${grey}": ${if(team.name === null) "${gold}null${grey}" else "\"${gold}${team.name}\"${grey}"},
          "${blue}id${grey}": "${gold}${team.id}${grey}",
          "${blue}created_date${grey}": "${gold}${team.created_date}${grey}"
        }
      """.trimIndent()
  }

  private fun inspectRoom(uuid: UUID, color: Boolean=true): String {
    return this.inspectRoom(Rooms.findById(uuid), color)
  }

  private fun inspectRoom(room: FCRoom?, color: Boolean=true): String {

    val (blue, gold, grey) = jsonColors(color)

    return if(room === null)
      "room not found"
    else
      """
        ${grey}{
          "${blue}name${grey}": ${if(room.name === null) "${gold}null${grey}" else "\"${gold}${room.name}\"${grey}"},
          "${blue}id${grey}": "${gold}${room.id}${grey}",
          "${blue}created_date${grey}": "${gold}${room.created_date}${grey}"
        }
      """.trimIndent()
  }

  private fun inspectRank(key: String, color: Boolean=true): String {
    return this.inspectRank(Ranks.findByKey(key), color)
  }

  private fun inspectRank(rank: FCRank?, color: Boolean=true): String {

    val (blue, gold, grey) = jsonColors(color)

    return if(rank === null)
      "rank not found"
    else
      """
        ${grey}{
          "${blue}key${grey}": "${gold}${rank.key}${grey}",
          "${blue}name${grey}": "${gold}${rank.name}${grey}",
          "${blue}description${grey}": ${if(rank.description === null) "${gold}null${grey}" else "\"${gold}${rank.description}\"${grey}"}
        }
      """.trimIndent()
  }

  private fun inspectTitle(key: String, color: Boolean=true): String {
    return this.inspectTitle(Titles.findByKey(key), color)
  }

  private fun inspectTitle(title: FCTitle?, color: Boolean=true): String {

    val (blue, gold, grey) = jsonColors(color)

    return if(title === null)
      "title not found"
    else
      """
        ${grey}{
          "${blue}key${grey}": "${gold}${title.key}${grey}",
          "${blue}name${grey}": "${gold}${title.name}${grey}",
          "${blue}description${grey}": ${if(title.description === null) "${gold}null${grey}" else "\"${gold}${title.description}\"${grey}"}
        }
      """.trimIndent()
  }
}