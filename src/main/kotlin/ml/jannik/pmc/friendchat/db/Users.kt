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

  private const val findSQLSegment       = "SELECT uuid, display_name, alt_of, fc_rank, selected_title, current_room, created_date, B.key as rank_key, B.name as rank_name, B.description as rank_description, B.color as rank_color, C.key as title_key, C.name as title_name, C.description as title_description FROM FC_User A LEFT JOIN FC_Rank B ON A.fc_rank = B.key LEFT JOIN FC_Title C ON A.selected_title = C.key"
  private const val findByUUIDSQL        = "$findSQLSegment WHERE uuid = ?"
  private const val findByDisplayNameSQL = "$findSQLSegment WHERE display_name = ?"

  private const val addToFriendlistSQL      = "INSERT INTO FC_CONN_Friends (user1, user2) VALUES (?, ?)"
  private const val removeFromFriendlistSQL = "DELETE FROM FC_CONN_Friends WHERE user1 = ? AND user2 = ? OR user1 = ? AND user2 = ?"
  private const val listFriendlistSQL       = "SELECT B.*, C.*, D.* FROM FC_CONN_Friends A, FC_User B LEFT JOIN FC_Rank C ON B.fc_rank = C.key LEFT JOIN FC_Title D ON B.selected_title = D.key WHERE (user1 = ? OR user2 = ?) AND (B.uuid = A.user1 OR B.uuid = A.user2) AND B.uuid != ?"

  private const val addToFriendRequestsSQL      = "INSERT INTO FC_Friendrequest (sender, receiver) VALUES (?, ?)"
  private const val removeFromFriendRequestsSQL = "DELETE FROM FC_Friendrequest WHERE sender = ? AND receiver = ?"
  private const val listFriendRequestsSQL       = "SELECT B.*, C.*, D.* FROM FC_Friendrequest A LEFT JOIN FC_User B ON A.sender = B.uuid LEFT JOIN FC_Rank C ON B.fc_rank = C.key LEFT JOIN FC_Title D ON B.selected_title = D.key WHERE A.receiver = ? ORDER BY A.created_date DESC"

  private const val addToBlockedlistSQL      = "INSERT INTO FC_CONN_Blocked (player, target) VALUES (?, ?)"
  private const val removeFromBlockedlistSQL = "DELETE FROM FC_CONN_Blocked WHERE player = ? AND target = ?"
  private const val listBlockedlistSQL       = "SELECT B.* FROM FC_CONN_Blocked A LEFT JOIN FC_User B ON A.target = B.uuid WHERE player = ?"

  public const val NUM_VALUES = 7

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

  fun constructPlayerFromResultSet(rs: ResultSet, offset: Int = 0): FCUser {
    return FCUser(
        uuid =           rs.getObject(offset + 1, UUID::class.java),
        display_name =   rs.getString(offset + 2),
        alt_of =         rs.getObject(offset + 3, UUID::class.java),
        rank =           Ranks.constructRankFromResultSet(rs, Users.NUM_VALUES),
        selected_title = Titles.constructTitleFromResultSet(rs, Users.NUM_VALUES + Ranks.NUM_VALUES),
        // omitting FCRoom; rs.getObject(offset + 6, UUID::class.java)
        created_date =   Date(rs.getTimestamp(offset + 7).getTime())
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

  fun addToFriendlist(self: UUID, target: UUID): Boolean {
    val addStmt: PreparedStatement = this.conn.prepareStatement(this.addToFriendlistSQL)

    addStmt.setObject(1, self)
    addStmt.setObject(2, target)

    addStmt.executeUpdate()

    return addStmt.getUpdateCount() == 1
  }

  fun removeFromFriendlist(self: UUID, target: UUID): Boolean {
    val removeStmt: PreparedStatement = this.conn.prepareStatement(this.removeFromFriendlistSQL)

    removeStmt.setObject(1, self)
    removeStmt.setObject(2, target)
    removeStmt.setObject(3, target)
    removeStmt.setObject(4, self)

    removeStmt.executeUpdate()

    return removeStmt.getUpdateCount() == 1
  }

  fun listFriendlist(player: UUID): List<FCUser> {
    val listStmt: PreparedStatement = this.conn.prepareStatement(this.listFriendlistSQL)

    listStmt.setObject(1, player)
    listStmt.setObject(2, player)
    listStmt.setObject(3, player)

    val rs = listStmt.executeQuery()

    val list = mutableListOf<FCUser>()

    while(rs.next())
      list.add(this.constructPlayerFromResultSet(rs))
    
    return list
  }

  fun listFriendRequests(player: UUID): List<FCUser> {
    val listStmt: PreparedStatement = this.conn.prepareStatement(this.listFriendRequestsSQL)

    listStmt.setObject(1, player)

    val rs = listStmt.executeQuery()

    val list = mutableListOf<FCUser>()

    while(rs.next())
      list.add(this.constructPlayerFromResultSet(rs))
    
    return list
  }

  fun addToFriendrequests(sender: UUID, receiver: UUID): Boolean {
    val addStmt: PreparedStatement = this.conn.prepareStatement(this.addToFriendlistSQL)

    addStmt.setObject(1, sender)
    addStmt.setObject(2, receiver)

    addStmt.executeUpdate()

    return addStmt.getUpdateCount() == 1
  }

  fun removeFromFriendrequests(sender: UUID, receiver: UUID): Boolean {
    val removeStmt: PreparedStatement = this.conn.prepareStatement(this.removeFromFriendlistSQL)

    removeStmt.setObject(1, sender)
    removeStmt.setObject(2, receiver)

    removeStmt.executeUpdate()

    return removeStmt.getUpdateCount() == 1
  }

  fun listBlockedlist(player: UUID): List<FCUser> {
    val listStmt: PreparedStatement = this.conn.prepareStatement(this.listBlockedlistSQL)

    listStmt.setObject(1, player)

    val rs = listStmt.executeQuery()

    val list = mutableListOf<FCUser>()

    while(rs.next())
      list.add(this.constructPlayerFromResultSet(rs))
    
    return list
  }

  fun addToBlockedlist(player: UUID, target: UUID): Boolean {
    val addStmt: PreparedStatement = this.conn.prepareStatement(this.addToBlockedlistSQL)

    addStmt.setObject(1, player)
    addStmt.setObject(2, target)

    addStmt.executeUpdate()

    return addStmt.getUpdateCount() == 1
  }

  fun removeFromBlockedlist(player: UUID, blocked: UUID): Boolean {
    val removeStmt: PreparedStatement = this.conn.prepareStatement(this.removeFromBlockedlistSQL)

    removeStmt.setObject(1, player)
    removeStmt.setObject(2, blocked)

    removeStmt.executeUpdate()

    return removeStmt.getUpdateCount() == 1
  }
}