package ml.jannik.pmc.friendchat.db

import java.util.UUID

import java.sql.*
import java.sql.Connection;

object Rooms {
  // TODO: load this from config.yml file
  private val host = "localhost"
  private val db = "friendchat"
  private val user = "friendchat"
  private val password = "password"
  
  private var conn: Connection = DB.connect(host, db, user, password)

  private const val createSQL   = "INSERT INTO FC_Room (name) VALUES (?) RETURNING id"
  private const val existsSQL   = "SELECT count(*) FROM FC_ROOM WHERE id = ?"
  private const val findByIdSQL = "SELECT id, name, created_date FROM FC_ROOM WHERE id = ?"

  fun create(room: _FCRoom): UUID {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.createSQL)

    stmt.setObject(1, room.name)

    val rs = stmt.executeQuery()
    rs.next()
    return rs.getObject(1, UUID::class.java)
  }

  fun exists(room: FCRoom): Boolean {
    return this.exists(room.id)
  }

  fun exists(uuid: UUID): Boolean {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.existsSQL)
    stmt.setObject(1, uuid)
    val rs = stmt.executeQuery()
    rs.next()
    val count = rs.getInt("count")
    return count == 1
  }

  private fun constructRoomFromStatement(rs: ResultSet): FCRoom? {
    return if(!rs.next())
      null
    else
      FCRoom(
        id = rs.getObject(1, UUID::class.java),
        name = rs.getString(2),
        created_date = rs.getDate(3)
      )
  }

  fun findById(uuid: UUID): FCRoom? {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.findByIdSQL)
    stmt.setObject(1, uuid)
    return this.constructRoomFromStatement(stmt.executeQuery())
  }
}