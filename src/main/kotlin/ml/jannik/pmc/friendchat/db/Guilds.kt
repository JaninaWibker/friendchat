package ml.jannik.pmc.friendchat.db

import java.util.UUID

import java.sql.*
import java.sql.Connection

data class FCUserWithJoinDate(val user: FCUser, val joined_date: Date)

object Guilds {
  // TODO: load this from config.yml file
  private val host = "localhost"
  private val db = "friendchat"
  private val user = "friendchat"
  private val password = "password"

  private var conn: Connection = DB.connect(host, db, user, password)

  private const val createSQL = "INSERT INTO FC_Guild (name, description, owner, room) VALUES (?, ?, ?, ?) RETURNING id"

  private const val existsSQLSegment = "SELECT count(*) as count FROM FC_Guild"
  private const val existsByIdSQL    = "$existsSQLSegment WHERE id = ?"
  private const val existsByNameSQL  = "$existsSQLSegment WHERE name = ?"

  private const val findSQLSegment = "SELECT id, name, description, owner, room, created_date FROM FC_Guild"
  private const val findByIdSQL    = "$findSQLSegment WHERE id = ?"
  private const val findByNameSQL  = "$findSQLSegment WHERE name = ?"

  private const val joinGuildSQL        = "INSERT INTO FC_CONN_GuildUser (fc_user, guild) VALUES (?, ?)"
  private const val leaveGuildSQL       = "DELETE FROM FC_CONN_GuildUser WHERE fc_user = ? AND guild = ?"
  private const val listInvitedGuildSQL = "SELECT B.*, C.*, D.* FROM FC_CONN_GuildInvitesUser A LEFT JOIN FC_User B ON A.fc_user = B.uuid LEFT JOIN FC_Rank C ON B.fc_rank = C.key LEFT JOIN FC_Title D ON B.selected_title = D.key WHERE A.guild = ?"
  private const val inviteGuildSQL      = "INSERT INTO FC_CONN_GuildInvitesUser (fc_user, guild) VALUES (?, ?)"
  private const val isInvitedGuildSQL   = "SELECT count(*) as count FROM FC_CONN_GuildInvitesUser WHERE fc_user = ? AND guild = ?"
  private const val declineGuildSQL     = "DELETE FROM FC_CONN_GuildInvitesUser WHERE fc_user = ? AND guild = ?"

  private const val listMembersSQLSegment = "SELECT B.*, C.*, D.*, A.created_date as joined_date FROM FC_CONN_GuildUser A LEFT JOIN FC_User B ON A.fc_user = B.uuid LEFT JOIN FC_Rank C ON B.fc_rank = C.key LEFT JOIN FC_Title D ON B.selected_title = D.key"
  private const val listMembersByIdSQL    = "$listMembersSQLSegment WHERE A.guild = ?"
  private const val listMembersByNameSQL  = "$listMembersSQLSegment LEFT JOIN FC_Guild E ON A.guild = E.id WHERE E.name = ?"

  private const val deleteGuildSQL = "DELETE FROM FC_Guild WHERE id = ?"

  private const val transferOwnershipGuildSQL = "UPDATE FC_Guild SET owner = ? WHERE id = ?"

  public const val NUM_VALUES = 6
  
  fun create(guild: _FCGuild): UUID {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.createSQL)

    val roomId: UUID = Rooms.create(_FCRoom("${guild.name}'s chat")) // TODO: maybe let this be somehow customizable (intl?)

    stmt.setString(1, guild.name)
    stmt.setString(2, guild.description)
    stmt.setObject(3, guild.owner)
    stmt.setObject(4, roomId)

    val rs = stmt.executeQuery()
    rs.next()

    val guildId: UUID = rs.getObject(1, UUID::class.java)

    val joinStmt: PreparedStatement = this.conn.prepareStatement(this.joinGuildSQL)
    joinStmt.setObject(1, guild.owner)
    joinStmt.setObject(2, guildId)

    joinStmt.executeUpdate()

    return guildId
  }

  fun exists(guild: FCGuild): Boolean {
    return this.exists(guild.id)
  }

  fun exists(uuid: UUID): Boolean {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.existsByIdSQL)
    stmt.setObject(1, uuid)
    val rs = stmt.executeQuery()
    rs.next()
    return rs.getInt("count") == 1
  }

  fun exists(name: String): Boolean {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.existsByNameSQL)
    stmt.setString(1, name)
    val rs = stmt.executeQuery()
    rs.next()
    return rs.getInt("count") == 1
  }

  private fun constructGuildFromStatement(rs: ResultSet): FCGuild? {
    return if(!rs.next()) null
    else                  this.constructGuildFromResultSet(rs)
  }

  fun constructGuildFromResultSet(rs: ResultSet, offset: Int = 0): FCGuild {
    return FCGuild(
      id = rs.getObject(offset + 1, UUID::class.java),
      name = rs.getString(offset + 2),
      description = rs.getString(offset + 3),
      owner = rs.getObject(offset + 4, UUID::class.java),
      room = rs.getObject(offset + 5, UUID::class.java),
      created_date = Date(rs.getTimestamp(offset + 6).getTime())
    )
  }

  fun findById(uuid: UUID): FCGuild? {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.findByIdSQL)
    stmt.setObject(1, uuid)
    return this.constructGuildFromStatement(stmt.executeQuery())
  }

  fun findByName(name: String): FCGuild? {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.findByNameSQL)
    stmt.setString(1, name)
    return this.constructGuildFromStatement(stmt.executeQuery())
  }

  private fun constructMemberListFromStatement(rs: ResultSet): List<FCUserWithJoinDate> {
    val list = mutableListOf<FCUserWithJoinDate>()
    
    while(rs.next()) list.add(FCUserWithJoinDate(
      user = Users.constructPlayerFromResultSet(rs),
      joined_date = Date(rs.getTimestamp("joined_date").getTime())
    ))

    return list
  }

  fun listMembersById(uuid: UUID): List<FCUserWithJoinDate> {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.listMembersByIdSQL)
    stmt.setObject(1, uuid)
    return this.constructMemberListFromStatement(stmt.executeQuery())
  }

  fun listMembersByName(name: String): List<FCUserWithJoinDate> {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.listMembersByNameSQL)
    stmt.setString(1, name)
    return this.constructMemberListFromStatement(stmt.executeQuery())
  }

  fun listInvitedGuild(guild: UUID): List<FCUser> {
    val listStmt: PreparedStatement = this.conn.prepareStatement(this.listInvitedGuildSQL)

    listStmt.setObject(1, guild)

    val list = mutableListOf<FCUser>()

    val rs = listStmt.executeQuery()

    while(rs.next()) list.add(Users.constructPlayerFromResultSet(rs))

    return list
  }

  fun inviteGuild(guild: UUID, player: UUID) {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.inviteGuildSQL)
    stmt.setObject(1, player)
    stmt.setObject(2, guild)

    stmt.executeUpdate()
  }

  fun isInvitedGuild(guild: UUID, player: UUID): Boolean {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.isInvitedGuildSQL)
    stmt.setObject(1, player)
    stmt.setObject(2, guild)

    val rs = stmt.executeQuery()

    rs.next()
    return rs.getInt("count") == 1
  }

  fun joinGuild(guild: UUID, player: UUID): Boolean {

    // joining a guild consists of first "declining" the invite -> removing invite
    // and after that adding the player to the guild member listj

    val declineStmt: PreparedStatement = this.conn.prepareStatement(this.declineGuildSQL)
    declineStmt.setObject(1, player)
    declineStmt.setObject(2, guild)

    declineStmt.executeUpdate()

    val joinStmt: PreparedStatement = this.conn.prepareStatement(this.joinGuildSQL)
    joinStmt.setObject(1, player)
    joinStmt.setObject(2, guild)

    joinStmt.executeUpdate()

    return joinStmt.getUpdateCount() == 1
  }

  fun leaveGuild(guild: UUID, player: UUID): Boolean {

    // this ignores the owner of the guild leaving. This has to be handled somewhere else

    val leaveStmt: PreparedStatement = this.conn.prepareStatement(this.leaveGuildSQL)
    leaveStmt.setObject(1, player)
    leaveStmt.setObject(2, guild)

    leaveStmt.executeUpdate()

    return leaveStmt.getUpdateCount() == 1
  }

  fun deleteGuild(guild: UUID): Boolean {
    val deleteStmt: PreparedStatement = this.conn.prepareStatement(this.deleteGuildSQL)

    deleteStmt.setObject(1, guild)

    deleteStmt.executeUpdate()

    return deleteStmt.getUpdateCount() == 1
  }

  fun transferOwnershipGuild(guild: UUID, new_owner: UUID): Boolean {
    val transferStmt: PreparedStatement = this.conn.prepareStatement(this.transferOwnershipGuildSQL)

    transferStmt.setObject(1, new_owner)
    transferStmt.setObject(2, guild)

    transferStmt.executeUpdate()

    return transferStmt.getUpdateCount() == 1
  }
}