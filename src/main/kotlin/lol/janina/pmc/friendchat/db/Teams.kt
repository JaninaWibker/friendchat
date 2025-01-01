package lol.janina.pmc.friendchat.db

import java.util.UUID

import java.sql.*
import java.sql.Connection;

object Teams {
  // TODO: load this from config.yml file
  private val host = "localhost"
  private val db = "friendchat"
  private val user = "friendchat"
  private val password = "password"

  private var conn: Connection = DB.connect(host, db, user, password)

  private const val createSQL = "INSERT INTO FC_Team (name, room) VALUES (?, ?) RETURNING id"

  private const val existsSQLSegment = "SELECT count(*) as count FROM FC_Team"
  private const val existsByIdSQL    = "${existsSQLSegment} WHERE id = ?"
  private const val existsByNameSQL  = "${existsSQLSegment} WHERE name = ?"

  private const val findByIdSQL    = "SELECT id, name, room, created_date FROM FC_Team WHERE id = ?"

  public const val NUM_VALUES = 4
  
  fun create(team: _FCTeam): UUID {

    val roomId: UUID = Rooms.create(_FCRoom(if(team.name === null) "team chat" else "${team.name}'s chat")) // TODO: maybe let this be somehow customizable (intl?)

    val stmt: PreparedStatement = this.conn.prepareStatement(this.createSQL)

    stmt.setString(1, team.name)
    stmt.setObject(2, roomId)

    val rs = stmt.executeQuery()
    rs.next()
    return rs.getObject(1, UUID::class.java)
  }

  fun exists(team: FCTeam): Boolean {
    return this.exists(team.id)
  }

  fun exists(uuid: UUID): Boolean {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.existsByIdSQL)
    stmt.setObject(1, uuid)
    val rs = stmt.executeQuery()
    rs.next()
    val count = rs.getInt("count")
    return count == 1
  }

  private fun constructTeamFromStatement(rs: ResultSet): FCTeam? {
    return if(!rs.next()) null
    else                  this.constructTeamFromResultSet(rs)
  }

  fun constructTeamFromResultSet(rs: ResultSet, offset: Int = 0): FCTeam {
    return FCTeam(
        id = rs.getObject(offset + 1, UUID::class.java),
        name = rs.getString(offset + 2),
        room = rs.getObject(offset + 3, UUID::class.java),
        created_date = Date(rs.getTimestamp(offset + 4).getTime())
      )
  }

  fun findById(uuid: UUID): FCTeam? {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.findByIdSQL)
    stmt.setObject(1, uuid)
    return this.constructTeamFromStatement(stmt.executeQuery())
  }
}