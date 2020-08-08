DO $$ BEGIN

  CREATE DOMAIN T_FC_User  AS UUID;
  CREATE DOMAIN T_FC_Guild AS UUID;
  CREATE DOMAIN T_FC_Team  AS UUID;
  CREATE DOMAIN T_FC_Room  AS UUID;
  CREATE DOMAIN T_FC_Rank  AS VARCHAR(32);
  CREATE DOMAIN T_FC_Title AS VARCHAR(32);

  CREATE DOMAIN FC_Date AS timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP;

  CREATE TYPE FC_Color AS ENUM (
    'aqua', 'black', 'blue', 'dark_aqua', 'dark_blue', 'dark_gray', 'dark_green', 'dark_purple',
    'dark_red', 'gold', 'gray', 'green', 'light_purple', 'red', 'white', 'yellow'
  );

EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

------- ROOM -------

CREATE TABLE IF NOT EXISTS FC_Room (
  id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(32), -- can be null
  is_default_room boolean DEFAULT FALSE,
  created_date FC_Date
);

INSERT INTO FC_Room (name, is_default_room) VALUES ( 'default', TRUE);

CREATE FUNCTION getDefaultRoom() RETURNS UUID
  AS 'SELECT id FROM FC_ROOM WHERE is_default_room = TRUE'
  LANGUAGE SQL
  IMMUTABLE;

------- RANK -------

CREATE TABLE IF NOT EXISTS FC_Rank (
  key  VARCHAR(32) PRIMARY KEY,
  name VARCHAR(32) UNIQUE NOT NULL,
  description VARCHAR(128) -- can be null
  color FC_Color NOT NULL DEFAULT 'white',
);

INSERT INTO FC_Rank ( key, name, color ) VALUES ( 'default',   'Default',   'white'       );
INSERT INTO FC_Rank ( key, name, color ) VALUES ( 'preferred', 'Preferred', 'blue'        );
INSERT INTO FC_Rank ( key, name, color ) VALUES ( 'moderator', 'Moderator', 'red'         );
INSERT INTO FC_Rank ( key, name, color ) VALUES ( 'owner',     'Owner',     'dark_purple' );

------- TITLE -------

CREATE TABLE IF NOT EXISTS FC_Title (
  key  VARCHAR(32) PRIMARY KEY,
  name VARCHAR(32) UNIQUE NOT NULL,
  description VARCHAR(128) -- can be null
);

INSERT INTO FC_Title ( key, name, description ) VALUES ( 'newby', 'Newby', 'joined the server' );

------- USER -------

CREATE TABLE IF NOT EXISTS FC_User (
  uuid           UUID PRIMARY KEY,
  display_name   VARCHAR(64) UNIQUE NOT NULL,
  alt_of         T_FC_User NOT NULL                   REFERENCES FC_User(uuid) ON DELETE RESTRICT, -- before deleting main account set this to itself
  fc_rank        T_FC_Rank NOT NULL DEFAULT 'default' REFERENCES FC_Rank(key)  ON DELETE RESTRICT, -- before deleting rank set appropriate new rank
  selected_title T_FC_Title         DEFAULT 'newby'   REFERENCES FC_Title(key) ON DELETE SET NULL, -- allow not displaying any title at all
  current_room   T_FC_Room NOT NULL DEFAULT getDefaultRoom(), -- TODO: should this be kept in the database or not?
  created_date   FC_Date
);

------- FRIEND REQUEST -------

CREATE TABLE IF NOT EXISTS FC_Friendrequest (
  sender   T_FC_User NOT NULL REFERENCES FC_User(uuid) ON DELETE CASCADE,
  receiver T_FC_User NOT NULL REFERENCES FC_User(uuid) ON DELETE CASCADE,
  created_date FC_Date,
  PRIMARY KEY(sender, receiver)
);

------- TEAM -------

CREATE TABLE IF NOT EXISTS FC_Team (
  id    UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name  VARCHAR(32), -- can be null
  room  T_FC_Room NOT NULL REFERENCES FC_Room(id) ON DELETE RESTRICT, -- TODO: is this correct?
  created_date FC_Date
);

------- GUILD -------

CREATE TABLE IF NOT EXISTS FC_Guild (
  id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name         VARCHAR(32) NOT NULL,
  description  VARCHAR(128), -- can be null
  owner        T_FC_User NOT NULL REFERENCES FC_User(uuid) ON DELETE RESTRICT, -- before deleting a new owner has to be chosen
  room         T_FC_Room NOT NULL REFERENCES FC_Room(id)   ON DELETE RESTRICT, -- TODO: is this correct?
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

CREATE TABLE IF NOT EXISTS FC_CONN_GuildInvitesUser (
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


---- TESTING -----

-- _jannik: a21fef49-5e6b-4105-9c8f-fc38cd78c835
-- _janina: c392ce5f-18d1-4d54-a7e7-5f0dc4037426

-- creating users (this just saves having to log in each reset with every account)
insert into fc_user ( uuid, display_name, alt_of, fc_rank, selected_title ) VALUES (
  'a21fef49-5e6b-4105-9c8f-fc38cd78c835', '_jannik', 'a21fef49-5e6b-4105-9c8f-fc38cd78c835', 'preferred', 'newby'
);
insert into fc_user ( uuid, display_name, alt_of, fc_rank, selected_title ) VALUES ( 
  'c392ce5f-18d1-4d54-a7e7-5f0dc4037426', '_Janina', 'c392ce5f-18d1-4d54-a7e7-5f0dc4037426', 'moderator', 'newby' 
);

-- auto-sending friend request from _jannik to _Janina and from _Janina to _jannik (technically illegal state but this allows easy testing from both accounts)
insert into fc_friendrequest ( sender, receiver ) VALUES ( 'a21fef49-5e6b-4105-9c8f-fc38cd78c835', 'c392ce5f-18d1-4d54-a7e7-5f0dc4037426' );
insert into fc_friendrequest ( sender, receiver ) VALUES ( 'c392ce5f-18d1-4d54-a7e7-5f0dc4037426', 'a21fef49-5e6b-4105-9c8f-fc38cd78c835' );