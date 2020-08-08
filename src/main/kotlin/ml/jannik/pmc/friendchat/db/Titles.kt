package ml.jannik.pmc.friendchat.db

import java.util.UUID

import java.sql.*
import java.sql.Connection;

object Titles {
  // TODO: load this from config.yml file
  private val host = "localhost"
  private val db = "friendchat"
  private val user = "friendchat"
  private val password = "password"

  private var conn: Connection = DB.connect(host, db, user, password)

  private const val createSQL    = "INSERT INTO FC_Title (key, name, description) VALUES (?, ?, ?)"
  private const val existsSQL    = "SELECT count(*) FROM FC_Title WHERE key = ?"
  private const val findByKeySQL = "SELECT key, name, description FROM FC_Title WHERE key = ?"

  fun create(title: FCTitle) {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.createSQL)

    stmt.setString(1, title.key)
    stmt.setString(2, title.name)
    stmt.setString(3, title.description)

    stmt.executeQuery()
  }

  fun exists(key: String): Boolean {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.existsSQL)
    stmt.setString(1, key)
    val rs = stmt.executeQuery()
    rs.next()
    val count = rs.getInt("count")
    return count == 1
  }

  fun exists(title: FCTitle): Boolean {
    return this.exists(title.key)
  }

  private fun constructTitleFromStatement(rs: ResultSet, offset: Int = 0): FCTitle? {
    return if(!rs.next())
      null
    else
      FCTitle(
        key = rs.getString(offset + 1),
        name = rs.getString(offset + 2),
        description = rs.getString(offset + 3)
      )
  }

  fun findByKey(key: String): FCTitle? {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.findByKeySQL)
    stmt.setString(1, key)
    return this.constructTitleFromStatement(stmt.executeQuery())
  }
}