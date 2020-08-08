package ml.jannik.pmc.friendchat.db

import java.util.UUID

import java.sql.*
import java.sql.Connection

import org.bukkit.ChatColor

object Ranks {
  // TODO: load this from config.yml file
  private val host = "localhost"
  private val db = "friendchat"
  private val user = "friendchat"
  private val password = "password"

  private var conn: Connection = DB.connect(host, db, user, password)

  private const val createSQL    = "INSERT INTO FC_Rank (key, name, description, color) VALUES (?, ?, ?, ?)"
  private const val existsSQL    = "SELECT count(*) FROM FC_Rank WHERE key = ?"
  private const val findByKeySQL = "SELECT key, name, description, color FROM FC_Rank WHERE key = ?"

  public const val NUM_VALUES = 4

  fun create(rank: FCRank) {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.createSQL)

    stmt.setString(1, rank.key)
    stmt.setString(2, rank.name)
    stmt.setString(3, rank.description)
    stmt.setString(4, rank.color.name.toLowerCase())

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

  fun exists(rank: FCRank): Boolean {
    return this.exists(rank.key)
  }

  private fun constructRankFromStatement(rs: ResultSet): FCRank? {
    return if(!rs.next()) null
    else                  this.constructRankFromResultSet(rs)
  }

  fun constructRankFromResultSet(rs: ResultSet, offset: Int = 0): FCRank {
    return FCRank(
        key = rs.getString(offset + 1),
        name = rs.getString(offset + 2),
        description = rs.getString(offset + 3),
        color = ChatColor.valueOf(rs.getString(offset + 4).toUpperCase())
      )
  }

  fun findByKey(key: String): FCRank? {
    val stmt: PreparedStatement = this.conn.prepareStatement(this.findByKeySQL)
    stmt.setString(1, key)
    return this.constructRankFromStatement(stmt.executeQuery())
  }
}