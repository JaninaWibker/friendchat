package ml.jannik.pmc.friendchat

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

import ml.jannik.pmc.friendchat.commands.other.*
import ml.jannik.pmc.friendchat.commands.friends.*
import ml.jannik.pmc.friendchat.commands.guilds.*
import ml.jannik.pmc.friendchat.commands.teams.*
import ml.jannik.pmc.friendchat.db.Users
import ml.jannik.pmc.friendchat.events.player.*


/**
 * The purpose of this plugin is to allow easy
 * communication between players as well as
 * create guilds.
 *
 * @author jannikwibker
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
class PluginEntry : JavaPlugin() {

  override fun onEnable() {
    this.saveDefaultConfig()

    // friend commands
    this.getCommand("friends")?.setExecutor(FriendListCommand())
    this.getCommand("friendrequest")?.setExecutor(FriendRequestCommand())
    this.getCommand("friendaccept")?.setExecutor(FriendAcceptCommand())
    this.getCommand("frienddecline")?.setExecutor(FriendDeclineCommand())
    this.getCommand("block")?.setExecutor(BlockCommand())
    this.getCommand("unblock")?.setExecutor(UnblockCommand())
    this.getCommand("friendchat")?.setExecutor(FriendChatCommand())
    this.getCommand("nofriendchat")?.setExecutor(NoFriendChatCommand())

    // guild commands
    this.getCommand("guildcreate")?.setExecutor(GuildCreateCommand())
    this.getCommand("guildinvite")?.setExecutor(GuildInviteCommand())
    this.getCommand("guildaccept")?.setExecutor(GuildAcceptCommand())
    this.getCommand("guilddecline")?.setExecutor(GuildDeclineCommand())
    this.getCommand("guildremove")?.setExecutor(GuildRemoveCommand())
    this.getCommand("guildmodify")?.setExecutor(GuildModifyCommand())
    this.getCommand("guildtransfer")?.setExecutor(GuildTransferCommand())
    this.getCommand("guildmembers")?.setExecutor(GuildMembersCommand())
    this.getCommand("guildprimary")?.setExecutor(GuildPrimaryCommand())
    this.getCommand("guildmsg")?.setExecutor(GuildMessageCommand())
    this.getCommand("guildchat")?.setExecutor(GuildChatCommand())
    this.getCommand("noguildchat")?.setExecutor(NoGuildChatCommand())

    // team commands
    this.getCommand("teamcreate")?.setExecutor(TeamCreateCommand())
    this.getCommand("teamjoin")?.setExecutor(TeamJoinCommand())
    this.getCommand("teamleave")?.setExecutor(TeamLeaveCommand())


    // other commands
    this.getCommand("r")?.setExecutor(ReplyCommand())
    this.getCommand("msg")?.setExecutor(MessageCommand())
    this.getCommand("nick")?.setExecutor(NickCommand())
    this.getCommand("unnick")?.setExecutor(UnNickCommand())

    this.getCommand("inspect")?.setExecutor(InspectCommand())

    // registering events
    val pm = Bukkit.getPluginManager()
    pm.registerEvents(JoinEvent(), this)
    pm.registerEvents(LeaveEvent(), this)
  }

  override fun onDisable() {
    Users.disconnect()
    // TODO : Do something if your plugin needs it (saving custom configs, clearing cache, closing connections...)
  }

}
