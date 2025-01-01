package lol.janina.pmc.friendchat.db

import lol.janina.pmc.friendchat.PluginEntry
import org.bukkit.Bukkit
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.system.exitProcess

object DB {
  private var conn: Connection? = null

  fun connect(host: String, db: String, user: String, password: String): Connection {
    if(this.conn === null) {
      Class.forName("org.postgresql.Driver")
      val tmp = DriverManager.getConnection("jdbc:postgresql://${host}/${db}", user, password)
      if(tmp === null) {
        Logger.getLogger(PluginEntry::class.java.name).log(Level.SEVERE, "could not connect to database")
        Bukkit.getServer().shutdown();
        exitProcess(1);
      } else {
        this.conn = tmp
      }
    }
    return this.conn!!
  }
  
  fun disconnect() {
    this.conn?.close()
  }
}