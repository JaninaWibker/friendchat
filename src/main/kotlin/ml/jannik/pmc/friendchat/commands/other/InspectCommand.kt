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
import java.util.UUID

import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.hover.content.Text

fun ComponentBuilder.jsonKey(key: String): ComponentBuilder {
  return this
    .append("\"").reset().color(ChatColor.GRAY)
    .append(key).color(ChatColor.BLUE)
    .append("\"").reset().color(ChatColor.GRAY)
}

fun ComponentBuilder.jsonString(value: String?): ComponentBuilder {

  return if(value === null)
    this
      .append("null").color(ChatColor.GOLD).append("").reset().color(ChatColor.GRAY)
  else
    this
      .append("\"").reset().color(ChatColor.GRAY)
      .append(value).color(ChatColor.GOLD)
      .append("\"").reset().color(ChatColor.GRAY)
}

fun ComponentBuilder.jsonTab(size: Int = 2): ComponentBuilder {
  return this.append(" ".repeat(size)).reset()
}

class InspectCommand : CommandExecutor {

  override fun onCommand(sender: CommandSender, command: Command, commandLabel: String, args: Array<String>): Boolean {

    if(args.size <= 1) return false // TODO: somehow tell the player what options are available at all. The "usage" string from the plugin.yml file is not really descriptive enough

    val uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"

    val uuid =
      if(args[1].matches(Regex(uuidRegex)))
        UUID.fromString(args[1])
      else
        null

    val msg: ComponentBuilder? = when(args[0]) {
      // tables
      "player" -> if(uuid !== null) this.inspectPlayer(Users.findByUUID(uuid))
                  else              this.inspectPlayer(Users.findByDisplayName(args[1]))
      "guild"  -> if(uuid !== null) this.inspectGuild(Guilds.findById(uuid))
                  else              this.inspectGuild(Guilds.findByName(args[1]))
      "team"   -> if(uuid !== null) this.inspectTeam(Teams.findById(uuid))
                  else null
      "room"   -> if(uuid !== null) this.inspectRoom(Rooms.findById(uuid))
                  else null
      "rank"   -> this.inspectRank(Ranks.findByKey(args[1]))
      "title"  -> this.inspectTitle(Titles.findByKey(args[1]))

      // connections
      "player-friends"        -> if(uuid !== null) this.inspectPlayerFriends(Users.findByUUID(uuid))
                                 else              this.inspectPlayerFriends(Users.findByDisplayName(args[1]))
      "player-friendrequests" -> if(uuid !== null) this.inspectPlayerFriendrequests(Users.findByUUID(uuid))
                                 else              this.inspectPlayerFriendrequests(Users.findByDisplayName(args[1]))
      "player-titles"         -> if(uuid !== null) this.inspectPlayerTitles(Users.findByUUID(uuid))
                                 else              this.inspectPlayerTitles(Users.findByDisplayName(args[1]))
      "player-guilds"         -> if(uuid !== null) this.inspectPlayerGuilds(Users.findByUUID(uuid))
                                 else              this.inspectPlayerGuilds(Users.findByDisplayName(args[1]))
      "player-teams"          -> if(uuid !== null) this.inspectPlayerTeams(Users.findByUUID(uuid))
                                 else              this.inspectPlayerTeams(Users.findByDisplayName(args[1]))
      "player-blockedlist"    -> if(uuid !== null) this.inspectPlayerBlocklist(Users.findByUUID(UUID.fromString(args[1])))
                                 else              this.inspectPlayerBlocklist(Users.findByDisplayName(args[1]))
      "guild-members"         -> if(uuid !== null) this.inspectGuildMembers(Guilds.findById(uuid))
                                 else              this.inspectGuildMembers(Guilds.findByName(args[1]))
      "guild-invites"         -> if(uuid !== null) this.inspectGuildInvites(Guilds.findById(uuid))
                                 else              this.inspectGuildInvites(Guilds.findByName(args[1]))
      "team-members"          -> if(uuid !== null) this.inspectTeamMembers(Teams.findById(uuid))
                                 else null
      
      else -> null
    }

    if(msg === null) {
      sender.sendMessage("\"${args[0]}\" is not a valid value")
      return false
    } else {
      sender.sendMessage(*msg.create())
      return true
    }
  }

  private fun inspectPlayer(player: FCUser?): ComponentBuilder {

    if(player === null) {
      return ComponentBuilder("player not found")
    } else {

      val hoverRank           = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${player.rank.name} (${player.rank.key})${if(player.rank.description !== null) "\n" + player.rank.description else ""}"))
      val hoverTitle          = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${player.selected_title?.name} (${player.selected_title?.key})\n${player.selected_title?.description}"))
      val hoverAlts           = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("click to inspect"))
      val hoverTitles         = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("click to inspect"))
      val hoverFriends        = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("click to inspect"))
      val hoverFriendrequests = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("click to inspect"))
      val hoverBlockedlist    = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("click to inspect"))
      val clickRank           = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/inspect rank ${player.rank.key}")
      val clickTitle          = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/inspect title ${player.selected_title?.key}")
      val clickAlts           = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect player-alts ${player.uuid}")
      val clickTitles         = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect player-titles ${player.uuid}")
      val clickFriends        = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect player-friends ${player.uuid}")
      val clickFriendrequests = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect player-friendrequests ${player.uuid}")
      val clickBlockedlist    = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect player-blockedlist ${player.uuid}")

      var unfinishedMessage = ComponentBuilder("{\n").color(ChatColor.GRAY)
        .jsonTab().jsonKey("display_name").append(": ").jsonString(player.display_name).append(",\n")
        .jsonTab().jsonKey("uuid").append(": ").jsonString(player.uuid.toString()).append(",\n")
        .jsonTab().jsonKey("alt_of").append(": ").jsonString(if(player.alt_of == player.uuid) "self" else player.alt_of.toString()).append(",\n")
        .jsonTab().jsonKey("alts").append(": ").append("[...]").italic(true).event(hoverAlts).event(clickAlts).append("").reset().color(ChatColor.GRAY).append(",\n")
        .jsonTab().jsonKey("rank").append(": ").append(player.rank.key).color(ChatColor.WHITE).event(hoverRank).event(clickRank).append("").reset().append("\n")
        .jsonTab().jsonKey("selected_title").append(": ")
      
      unfinishedMessage =
        if(player.selected_title !== null)
          unfinishedMessage.append(player.selected_title.key).color(ChatColor.WHITE).event(hoverTitle).event(clickTitle)
        else
          unfinishedMessage.color(ChatColor.GRAY).italic(true).append("none")

      return unfinishedMessage
        .append("").reset().append("\n")
        .jsonTab().jsonKey("titles").append(": ").append("[...]").italic(true).event(hoverTitles).event(clickTitles).append("").reset().color(ChatColor.GRAY).append(",\n")
        .jsonTab().jsonKey("friends").append(": ").append("[...]").italic(true).event(hoverFriends).event(clickFriends).append("").reset().color(ChatColor.GRAY).append(",\n")
        .jsonTab().jsonKey("friendrequests").append(": ").append("[...]").italic(true).event(hoverFriendrequests).event(clickFriendrequests).append("").reset().color(ChatColor.GRAY).append(",\n")
        .jsonTab().jsonKey("blocked").append(": ").append("[...]").italic(true).event(hoverBlockedlist).event(clickBlockedlist).append("").reset().color(ChatColor.GRAY).append(",\n")
        .jsonTab().jsonKey("created_date").append(": ").jsonString(player.created_date.toString()).append("\n")
        .append("}")
    }
  }

  private fun inspectGuild(guild: FCGuild?): ComponentBuilder {

    if(guild === null) {
      return ComponentBuilder("guild not found")
    } else {

      val hoverMembers = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("click to inspect"))
      val hoverInvites = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("click to inspect"))
      val clickMembers = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect guild-members ${guild.id}")
      val clickInvites = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect guild-invites ${guild.id}")

      return ComponentBuilder("{\n").color(ChatColor.GRAY)
        .jsonTab().jsonKey("name").append(": ").jsonString(guild.name).append(",\n")
        .jsonTab().jsonKey("id").append(": ").jsonString(guild.id.toString()).append(",\n")
        .jsonTab().jsonKey("description").append(": ").jsonString(guild.description).append(",\n")
        .jsonTab().jsonKey("owner").append(": ").jsonString(guild.owner.toString()).append(",\n")
        .jsonTab().jsonKey("room").append(": ").jsonString(guild.room.toString()).append(",\n")
        .jsonTab().jsonKey("members").append(": ").append("[...]").italic(true).event(hoverMembers).event(clickMembers).append("").reset().color(ChatColor.GRAY).append(",\n")
        .jsonTab().jsonKey("invites").append(": ").append("[...]").italic(true).event(hoverInvites).event(clickInvites).append("").reset().color(ChatColor.GRAY).append(",\n")
        .jsonTab().jsonKey("created_date").append(": ").jsonString(guild.created_date.toString()).append("\n")
        .append("}")
    }
  }

  private fun inspectTeam(team: FCTeam?): ComponentBuilder {

    if(team === null) {
      return ComponentBuilder("team not found")
    } else {

      val hoverMembers = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("click to inspect"))
      val clickMembers = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/inspect team-members ${team.id}")

      return ComponentBuilder("{\n").color(ChatColor.GRAY)
        .jsonTab().jsonKey("name").append(": ").jsonString(team.name).append(",\n")
        .jsonTab().jsonKey("id").append(": ").jsonString(team.id.toString()).append(",\n")
        .jsonTab().jsonKey("members").append(": ").append("[...]").italic(true).event(hoverMembers).event(clickMembers).append("").reset().color(ChatColor.GRAY).append(",\n")
        .jsonTab().jsonKey("created_date").append(": ").jsonString(team.created_date.toString()).append("\n")
        .append("}")

    }
  }

  private fun inspectRoom(room: FCRoom?): ComponentBuilder {

    return if(room === null)
      ComponentBuilder("room not found")
    else
      ComponentBuilder("{\n").color(ChatColor.GRAY)
        .jsonTab().jsonKey("name").append(": ").jsonString(room.name).append(",\n")
        .jsonTab().jsonKey("id").append(": ").jsonString(room.id.toString()).append(",\n")
        .jsonTab().jsonKey("created_date").append(": ").jsonString(room.created_date.toString()).append("\n")
        .append("}")
  }

  private fun inspectRank(rank: FCRank?): ComponentBuilder {

    return if(rank === null)
      ComponentBuilder("rank not found")
    else
      ComponentBuilder("{\n").color(ChatColor.GRAY)
        .jsonTab().jsonKey("key").append(": ").jsonString(rank.key).append(",\n")
        .jsonTab().jsonKey("name").append(": ").jsonString(rank.name).append(",\n")
        .jsonTab().jsonKey("description").append(": ").jsonString(rank.description).append(",\n")
        .jsonTab().jsonKey("color").append(": ").jsonString(rank.color.name.toLowerCase()).append("\n")
        .append("}")
  }

  private fun inspectTitle(title: FCTitle?): ComponentBuilder {

    return if(title === null)
      ComponentBuilder("title not found")
    else
      ComponentBuilder("{\n").color(ChatColor.GRAY)
        .jsonTab().jsonKey("key").append(": ").jsonString(title.key).append(",\n")
        .jsonTab().jsonKey("name").append(": ").jsonString(title.name).append(",\n")
        .jsonTab().jsonKey("description").append(": ").jsonString(title.description).append("\n")
        .append("}")
  }

  private fun inspectPlayerList(players: List<FCUser>, initialComp: ComponentBuilder? = null): ComponentBuilder { // TODO: improve (especially add player rank color thingy)

    val comp =
      if(initialComp === null)
        ComponentBuilder("[").color(ChatColor.GRAY)
      else
        initialComp.append("[").color(ChatColor.GRAY)

    return players.fold(comp) { acc, user ->
      acc.append("\n").append(user.display_name)
    }.append("\n]")
  }

  private fun inspectPlayerFriends(player: FCUser?): ComponentBuilder {
    return if(player === null) {
      ComponentBuilder("player not found")
    } else {
      this.inspectPlayerList(Users.listFriendlist(player.uuid), ComponentBuilder("friends: "))
    }
  }

  private fun inspectPlayerFriendrequests(player: FCUser?): ComponentBuilder {
    return if(player === null) {
      ComponentBuilder("player not found")
    } else {
      this.inspectPlayerList(Users.listFriendRequests(player.uuid), ComponentBuilder("friendrequests: "))
    }
  }

  private fun inspectPlayerTitles(player: FCUser?): ComponentBuilder {
    if(player === null) {
      return ComponentBuilder("player not found")
    } else {
      return ComponentBuilder("WIP") // TODO
    }
  }

  private fun inspectPlayerGuilds(player: FCUser?): ComponentBuilder {
    if(player === null) {
      return ComponentBuilder("player not found")
    } else {
      return ComponentBuilder("WIP") // TODO
    }
  }

  private fun inspectPlayerTeams(player: FCUser?): ComponentBuilder {
    if(player === null) {
      return ComponentBuilder("player not found")
    } else {
      return ComponentBuilder("WIP") // TODO
    }
  }

  private fun inspectPlayerBlocklist(player: FCUser?): ComponentBuilder {
    if(player === null) {
      return ComponentBuilder("player not found")
    } else {
      return this.inspectPlayerList(Users.listBlockedlist(player.uuid), ComponentBuilder("blockedlist: "))
    }
  }

  private fun inspectGuildMembers(guild: FCGuild?): ComponentBuilder {
    return if(guild === null) {
      ComponentBuilder("guild not found")
    } else {
      this.inspectPlayerList(Guilds.listMembersById(guild.id).map { it.user }, ComponentBuilder("members: "))
    }
  }

  private fun inspectGuildInvites(guild: FCGuild?): ComponentBuilder {
    return if(guild === null) {
      ComponentBuilder("guild not found")
    } else {
      this.inspectPlayerList(Guilds.listInvitedGuild(guild.id), ComponentBuilder("invites: "))
    }
  }

  private fun inspectTeamMembers(team: FCTeam?): ComponentBuilder {
    if(team === null) {
      return ComponentBuilder("team not found")
    } else {
      return ComponentBuilder("WIP") // TODO
    }
  }

}