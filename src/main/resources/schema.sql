DROP INDEX IF EXISTS index_users_on_email;
DROP INDEX IF EXISTS index_users_on_nickname;
DROP INDEX IF EXISTS index_forum_on_slug;

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS forums CASCADE;

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


CREATE TABLE IF NOT EXISTS forums (
  id      BIGSERIAL PRIMARY KEY,
  posts   INT                                     NOT NULL DEFAULT 0,
  slug    VARCHAR(50),
  threads INT                                     NOT NULL DEFAULT 0,
  title   VARCHAR(100)                            NOT NULL,
  user_id BIGINT REFERENCES users (id)            NOT NULL
);

CREATE UNIQUE INDEX index_forum_on_slug
  ON forums (LOWER(slug));

CREATE INDEX index_forum_on_user_id
  ON forums (user_id);
