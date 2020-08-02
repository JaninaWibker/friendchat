package ml.jannik.pmc.friendchat.db

import java.util.UUID

import java.sql.*
import java.sql.Connection;

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

  private const val joinGuildSQL = "INSERT INTO FC_CONN_GuildUser (fc_user, guild) VALUES (?, ?)"

  private const val listMembersSQLSegment = "SELECT B.* FROM FC_CONN_GuildUser A LEFT JOIN FC_User B ON A.fc_user = B.uuid"
  private const val listMembersByIdSQL    = "$listMembersSQLSegment WHERE A.guild = ?"
  private const val listMembersByNameSQL  = "$listMembersSQLSegment LEFT JOIN FC_Guild C ON A.guild = C.id WHERE C.name = ?"
  
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
    val count = rs.getInt("count")
    return count == 1
  }

  fun exists(name: String): Boolean {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.existsByNameSQL)
    stmt.setString(1, name)
    val rs = stmt.executeQuery()
    rs.next()
    val count = rs.getInt("count")
    return count == 1
  }

  private fun constructGuildFromStatement(rs: ResultSet): FCGuild? {
    return if(!rs.next())
      null
    else
      FCGuild(
        id = rs.getObject(1, UUID::class.java),
        name = rs.getString(2),
        description = rs.getString(3),
        owner = rs.getObject(4, UUID::class.java),
        room = rs.getObject(5, UUID::class.java),
        created_date = rs.getDate(6)
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
}