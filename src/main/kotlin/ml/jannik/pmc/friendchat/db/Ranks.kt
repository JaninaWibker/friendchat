package ml.jannik.pmc.friendchat.db

import java.util.UUID

import java.sql.*
import java.sql.Connection;

object Ranks {
  // TODO: load this from config.yml file
  private val host = "localhost"
  private val db = "friendchat"
  private val user = "friendchat"
  private val password = "password"

  private var conn: Connection = DB.connect(host, db, user, password)

  private const val createSQL    = "INSERT INTO FC_Rank (key, name, description) VALUES (?, ?, ?)"
  private const val existsSQL    = "SELECT count(*) FROM FC_Rank WHERE key = ?"
  private const val findByKeySQL = "SELECT key, name, description FROM FC_Rank WHERE key = ?"

  fun create(rank: FCRank) {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.createSQL)

    stmt.setString(1, rank.key)
    stmt.setString(2, rank.name)
    stmt.setString(3, rank.description)

    stmt.executeQuery()
  }

  fun exists(key: String): Boolean {
    val stmt: PreparedStatement = conn.prepareStatement(this.existsSQL)
    stmt.setString(1, key)
    val rs = stmt.executeQuery()
    rs.next()
    val count = rs.getInt("count")
    return count == 1
  }

  fun exists(rank: FCRank): Boolean {
    return this.exists(rank.key)
  }

  private fun constructRankFromStatement(rs: ResultSet): FCRank? {
    return if(!rs.next())
      null
    else
      FCRank(
        key = rs.getString(1),
        name = rs.getString(2),
        description = rs.getString(3)
      )
  }

  fun findByKey(key: String): FCRank? {
    val stmt: PreparedStatement = conn.prepareStatement(this.findByKeySQL)
    stmt.setString(1, key)
    return this.constructRankFromStatement(stmt.executeQuery())
  }
}