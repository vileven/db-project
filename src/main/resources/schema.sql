DROP INDEX IF EXISTS index_users_on_email;
DROP INDEX IF EXISTS index_users_on_nickname;
DROP INDEX IF EXISTS index_forum_on_slug;
DROP INDEX IF EXISTS index_threads_on_author_id;
DROP INDEX IF EXISTS index_threads_on_forum_id;
DROP INDEX IF EXISTS index_threads_on_slug;
DROP INDEX IF EXISTS index_posts_on_author_id;
DROP INDEX IF EXISTS index_posts_on_forum_id;
DROP INDEX IF EXISTS index_posts_on_parent;
DROP INDEX IF EXISTS index_posts_on_thread_id;
DROP INDEX IF EXISTS index_posts_on_path;


DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS forums CASCADE;
DROP TABLE IF EXISTS threads CASCADE;
DROP TABLE IF EXISTS posts CASCADE ;

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

CREATE TABLE IF NOT EXISTS threads (
  id        BIGSERIAL PRIMARY KEY,
  author_id BIGINT REFERENCES users (id),
  created   TIMESTAMPTZ  NOT NULL,
  forum_id  BIGINT REFERENCES forums (id),
  message   TEXT         NOT NULL,
  slug      VARCHAR(50) UNIQUE,
  title     VARCHAR(100) NOT NULL,
  votes     INT          NOT NULL DEFAULT 0
);

CREATE INDEX index_threads_on_author_id
  ON threads (author_id);

CREATE INDEX index_threads_on_forum_id
  ON threads (forum_id);

CREATE UNIQUE INDEX index_threads_on_slug
  ON threads (LOWER(slug));


CREATE TABLE IF NOT EXISTS posts (
  id        BIGSERIAL PRIMARY KEY,
  author_id BIGINT REFERENCES users (id)   NOT NULL,
  created   TIMESTAMPTZ                    NOT NULL,
  forum_id  BIGINT REFERENCES forums (id)  NOT NULL,
  is_edited BOOLEAN DEFAULT FALSE,
  message   TEXT,
  parent    BIGINT REFERENCES posts (id),
  path      BIGINT []                      NOT NULL,
  thread_id BIGINT REFERENCES threads (id) NOT NULL
) ;

CREATE INDEX index_posts_on_path
  ON posts USING GIN (path);

CREATE INDEX index_posts_on_parent
  ON posts (parent) ;

CREATE INDEX index_posts_on_author_id
  ON posts (author_id);

CREATE INDEX index_posts_on_forum_id
  ON posts (forum_id);

CREATE INDEX index_posts_on_thread_id
  ON posts(thread_id);
