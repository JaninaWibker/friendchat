DO $$ BEGIN

  CREATE DOMAIN T_FC_User  AS UUID;
  CREATE DOMAIN T_FC_Guild AS UUID;
  CREATE DOMAIN T_FC_Team  AS UUID;
  CREATE DOMAIN T_FC_Room  AS UUID;
  CREATE DOMAIN T_FC_Rank  AS VARCHAR(32);
  CREATE DOMAIN T_FC_Title AS VARCHAR(32);

  CREATE DOMAIN FC_Date AS timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP;

EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS FC_Room (
  id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(32), -- can be null
  created_date FC_Date
);

CREATE TABLE IF NOT EXISTS FC_Rank (
  key  VARCHAR(32) PRIMARY KEY,
  name VARCHAR(32) UNIQUE NOT NULL,
  description VARCHAR(128)
);

INSERT INTO FC_Rank ( key, name ) VALUES ( 'default', 'Default' );

CREATE TABLE IF NOT EXISTS FC_Title (
  key  VARCHAR(32) PRIMARY KEY,
  name VARCHAR(32) UNIQUE NOT NULL,
  description VARCHAR(128)
);

INSERT INTO FC_Title ( key, name, description ) VALUES ( 'newby', 'Newby', 'joined the server' );

CREATE TABLE IF NOT EXISTS FC_User (
  uuid           UUID PRIMARY KEY,
  display_name   VARCHAR(64) UNIQUE NOT NULL,
  alt_of         T_FC_User NOT NULL                   REFERENCES FC_User(uuid) ON DELETE RESTRICT, -- before deleting main account set this to itself
  fc_rank        T_FC_Rank NOT NULL DEFAULT 'default' REFERENCES FC_Rank(key)  ON DELETE RESTRICT, -- before deleting rank set appropriate new rank
  selected_title T_FC_Title         DEFAULT 'newby'   REFERENCES FC_Title(key) ON DELETE SET NULL, -- allow not displaying any title at all
  created_date   FC_Date
);

CREATE TABLE IF NOT EXISTS FC_Friendrequest (
  sender   T_FC_User NOT NULL REFERENCES FC_User(uuid) ON DELETE CASCADE,
  receiver T_FC_User NOT NULL REFERENCES FC_User(uuid) ON DELETE CASCADE,
  created_date FC_Date,
  PRIMARY KEY(sender, receiver)
);

CREATE TABLE IF NOT EXISTS FC_Team (
  id    UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name  VARCHAR(32), -- can be null
  room  T_FC_Room NOT NULL REFERENCES FC_Room(id) ON DELETE RESTRICT, -- TODO: is this correct?
  created_date FC_Date
);

CREATE TABLE IF NOT EXISTS FC_Guild (
  id           UUID PRIMARY KEY,
  name         VARCHAR(32) NOT NULL,
  description  VARCHAR(128), -- can be null
  owner        T_FC_User NOT NULL REFERENCES FC_User(uuid) ON DELETE RESTRICT, -- before deleting a new owner has to be chosen
  created_date FC_Date
);

------- CONNECTIONS -------

CREATE TABLE IF NOT EXISTS FC_CONN_Friends (
  user1 T_FC_User REFERENCES FC_User(uuid) ON DELETE CASCADE,
  user2 T_FC_User REFERENCES FC_User(uuid) ON DELETE CASCADE,
  created_date FC_Date,
  PRIMARY KEY(user1, user2)
);

CREATE TABLE IF NOT EXISTS FC_CONN_TeamUser (
  fc_user T_FC_User REFERENCES FC_User(uuid) ON DELETE CASCADE,
  team    T_FC_Team REFERENCES FC_Team(id)   ON DELETE CASCADE,
  created_date FC_Date,
  PRIMARY KEY(fc_user, team)
);

CREATE TABLE IF NOT EXISTS FC_CONN_GuildUser (
  fc_user T_FC_User  REFERENCES FC_User(uuid) ON DELETE CASCADE,
  guild   T_FC_Guild REFERENCES FC_Guild(id)  ON DELETE CASCADE,
  created_date FC_Date,
  PRIMARY KEY(fc_user, guild)
);

CREATE TABLE IF NOT EXISTS FC_CONN_RoomUser (
  fc_user T_FC_User REFERENCES FC_User(uuid) ON DELETE CASCADE,
  room    T_FC_Room REFERENCES FC_Room(id)   ON DELETE CASCADE,
  PRIMARY KEY(fc_user, room)
);

CREATE TABLE IF NOT EXISTS FC_CONN_UserTitle (
  fc_user T_FC_User  REFERENCES FC_User(uuid) ON DELETE CASCADE,
  title   T_FC_Title REFERENCES FC_Title(key) ON DELETE CASCADE,
  created_date FC_Date,
  PRIMARY KEY(fc_user, title)
);
