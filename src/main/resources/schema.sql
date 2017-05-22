DROP TABLE IF EXISTS vote CASCADE;
DROP TABLE IF EXISTS post CASCADE;
DROP TABLE IF EXISTS thread CASCADE;
DROP TABLE IF EXISTS forum CASCADE;
DROP INDEX IF EXISTS unique_email;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS users_forum CASCADE;

DROP INDEX IF EXISTS unique_slug_thread;
DROP INDEX IF EXISTS unique_slug_forum;
DROP INDEX IF EXISTS unique_nickname;
DROP INDEX IF EXISTS idx_forum_user;
DROP INDEX IF EXISTS idx_thread_user;
DROP INDEX IF EXISTS idx_thread_forum;
DROP INDEX IF EXISTS idx_post_user;
DROP INDEX IF EXISTS idx_post_forum_id;
DROP INDEX IF EXISTS idx_uf_forum;
DROP INDEX IF EXISTS idx_uf_user;
DROP INDEX IF EXISTS idx_post_thread_id;
DROP INDEX IF EXISTS idx_post_parent_thread;
DROP INDEX IF EXISTS idx_post_parent;
DROP INDEX IF EXISTS idx_post_id_thread_id;

DROP TRIGGER IF EXISTS post_insert_trigger ON post;
DROP TRIGGER IF EXISTS thread_insert_trigger ON thread;


DROP TABLE IF EXISTS users CASCADE ;
DROP INDEX IF EXISTS index_users_on_email;
DROP INDEX IF EXISTS index_users_on_nickname;

CREATE TABLE IF NOT EXISTS users (
  id       BIGSERIAL PRIMARY KEY,
  nickname VARCHAR(50)  NOT NULL UNIQUE,
  fullname VARCHAR(100) NOT NULL,
  email    VARCHAR(50)  NOT NULL UNIQUE,
  about    TEXT
);

CREATE UNIQUE INDEX index_users_on_nickname
  ON users (LOWER(nickname));

CREATE UNIQUE INDEX index_users_on_email
  ON users (LOWER(email));
