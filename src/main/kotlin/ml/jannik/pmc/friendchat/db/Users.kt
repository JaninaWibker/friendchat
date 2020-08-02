package ml.jannik.pmc.friendchat.db

import java.util.UUID

import java.sql.*
import java.sql.Connection;

object Users {
  // TODO: load this from config.yml file
  private val host = "localhost"
  private val db = "friendchat"
  private val user = "friendchat"
  private val password = "password"

  private var conn: Connection = DB.connect(host, db, user, password)

  private const val createSQL     = "INSERT INTO FC_User (uuid, display_name, alt_of) VALUES (?, ?, ?)"
  private const val createSQLFull = "INSERT INTO FC_User (uuid, display_name, alt_of, fc_rank, selected_title) VALUES (?, ?, ?, ?, ?)"

  private const val updateDisplayNameSQL = "UPDATE FC_User SET display_name = ? WHERE uuid = ?"

  private const val existsSQL = "SELECT count(*) as count FROM FC_User WHERE uuid = ?"

  private const val findSQLSegment       = "SELECT uuid, display_name, alt_of, fc_rank, selected_title, created_date FROM FC_User"
  private const val findByUUIDSQL        = "$findSQLSegment WHERE uuid = ?"
  private const val findByDisplayNameSQL = "$findSQLSegment WHERE display_name = ?"

  fun create(user: _FCUser) {

    val stmt: PreparedStatement = this.conn.prepareStatement(if(user.rank === null) this.createSQL else this.createSQLFull)

    stmt.setObject(1, user.uuid)
    stmt.setString(2, user.display_name)
    stmt.setObject(3, user.alt_of)

    if(user.rank !== null) {
      stmt.setObject(4, user.rank)
      stmt.setObject(5, user.selected_title) 
    }
    stmt.executeUpdate()
  }

  fun updateDisplayName(user: _FCUser) {
    val updateStmt: PreparedStatement = this.conn.prepareStatement(this.updateDisplayNameSQL)

    updateStmt.setString(1, user.display_name)
    updateStmt.setObject(2, user.uuid)

    updateStmt.executeUpdate()
  }

  fun exists(user: FCUser): Boolean {
    return this.exists(user.uuid)
  }

  fun exists(uuid: UUID): Boolean {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.existsSQL)
    stmt.setObject(1, uuid)
    val rs = stmt.executeQuery()
    rs.next()
    val count = rs.getInt("count");
    return count == 1
  }

  private fun constructPlayerFromStatement(rs: ResultSet): FCUser? {
    return if(!rs.next()) null
    else                  this.constructPlayerFromResultSet(rs)
  }

  fun constructPlayerFromResultSet(rs: ResultSet): FCUser {
    return FCUser(
        uuid = rs.getObject(1, UUID::class.java),
        display_name = rs.getString(2),
        alt_of = rs.getObject(3, UUID::class.java),
        rank = rs.getString(4),
        selected_title = rs.getString(5),
        created_date = Date(rs.getTimestamp(6).getTime())
    )
  }

  fun findByDisplayName(display_name: String): FCUser? {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.findByDisplayNameSQL)
    stmt.setString(1, display_name)
    return this.constructPlayerFromStatement(stmt.executeQuery())
  }

  fun findByUUID(uuid: UUID): FCUser? {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.findByUUIDSQL)
    stmt.setObject(1, uuid)
    return this.constructPlayerFromStatement(stmt.executeQuery())
  }
}