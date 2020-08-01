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

  private const val createSQL = "INSERT INTO FC_Guild (name, description, owner) VALUES (?, ?, ?)"

  private const val existsSQLSegment = "SELECT count(*) as count FROM FC_Guild"
  private const val existsByIdSQL    = "${existsSQLSegment} WHERE id = ?"
  private const val existsByNameSQL  = "${existsSQLSegment} WHERE name = ?"

  private const val findSQLSegment = "SELECT id, name, description, owner, created_date FROM FC_Guild"
  private const val findByIdSQL    = "${findSQLSegment} WHERE id = ?"
  private const val findByNameSQL  = "${findSQLSegment} WHERE name = ?"
  
  fun create(guild: FCGuild) {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.createSQL)

    stmt.setString(1, guild.name)
    stmt.setString(2, guild.description)
    stmt.setObject(3, guild.owner)

    stmt.executeUpdate()
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
        created_date = rs.getDate(5)
      )
  }

  fun findById(uuid: UUID): FCGuild? {
    val stmt: PreparedStatement = conn.prepareStatement(findByIdSQL)
    stmt.setObject(1, uuid)
    return this.constructGuildFromStatement(stmt.executeQuery())
  }

  fun findByName(name: String): FCGuild? {
    val stmt: PreparedStatement = conn.prepareStatement(findByNameSQL)
    stmt.setString(1, name)
    return this.constructGuildFromStatement(stmt.executeQuery())
  }
}