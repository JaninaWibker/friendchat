package ml.jannik.pmc.friendchat.commands.other

import ml.jannik.pmc.friendchat.db.FCUser
import ml.jannik.pmc.friendchat.db.Users
import org.bukkit.command.CommandExecutor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.UUID;

class InspectCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    val uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"

    val msg: String? = when(args[0]) {
      "player" -> if(args[1].matches(Regex(uuidRegex))) this.inspectPlayer(UUID.fromString(args[1]), true)
                  else this.inspectPlayer(args[1], true)
      "guild"  -> "not implemented yet"
      "team"   -> "not implemented yet"
      "room"   -> "not implemented yet"
      "rank"   -> "not implemented yet"
      "title"  -> "not implemented yet"
      else     -> null
    }

    sender.sendMessage(if(msg === null) "not found" else msg)
    return msg !== null
  }

  private fun inspectPlayer(displayName: String, color: Boolean=true): String {
    return inspectPlayer(Users.findByDisplayName(displayName), color)
  }

  private fun inspectPlayer(uuid: UUID, color: Boolean=true): String {
    return inspectPlayer(Users.findByUUID(uuid), color)
  }

  private fun inspectPlayer(player: FCUser?, color: Boolean =true): String {

    val blue = if(color) "§9" else ""
    val gold = if(color) "§6" else ""
    val grey = if(color) "§7" else ""
    return if(player === null)
      "player not found"
    else
      """
        ${grey}{
          "${blue}display_name${grey}": "${gold}${player.display_name}${grey}",
          "${blue}uuid${grey}":   "${gold}${player.uuid}${grey}",
          "${blue}alt_of${grey}": "${gold}${if(player.alt_of == player.uuid) "self" else player.alt_of.toString()}${grey}",
          "${blue}rank${grey}":    ${player.rank},
          "${blue}title${grey}":   ${player.selected_title},
          "${blue}created_date${grey}": "${gold}${player.created_date}${grey}"
        }
      """.trimIndent()
  }
}