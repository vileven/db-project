package api.repositories;

import api.models.Post;
import api.models.ThreadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Created by Vileven on 23.05.17.
 */
@Transactional
@Repository
public class PostRepository {
    private final JdbcTemplate template;

    @Autowired
    public PostRepository(JdbcTemplate template) {
        this.template = template;
    }

    public static final RowMapper<Post> POST_MAP = (rs, rowNum) -> new Post(rs.getLong("id"), rs.getString("author"),
            LocalDateTime.ofInstant(rs.getTimestamp("created").toInstant(),
                    ZoneOffset.ofHours(0)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
            rs.getString("forum"), rs.getString("message"), rs.getLong("thread_id"),
            rs.getLong("parent"),
            rs.getBoolean("is_edited"));

    public List<Post> createBatch(List<Post> postsInfo, ThreadModel thread) throws SQLException {
        final String query = "INSERT INTO posts " +
                "(id, author_id, created, forum_id, is_edited, message, parent, path, thread_id)" +
                " VALUES (?, (SELECT u.id FROM users u WHERE lower(u.nickname) = lower(?)), ?::TIMESTAMPTZ, " +
                "(SELECT f.id FROM forums f WHERE lower(f.slug) = lower(?)), ?, ?, ?," +
                " (SELECT path FROM posts WHERE id = ?) || ?::BIGINT, ?) " ;

        final String created = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        try(Connection conn = template.getDataSource().getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.NO_GENERATED_KEYS)) {

            for (final Post post : postsInfo) {
                post.setId(template.queryForObject("SELECT nextval('posts_id_seq')", Long.class));
                post.setForum(thread.getForum());
                post.setThread(thread.getId());
                post.setCreated(created);
                try {
                    if (post.getParent() != null && !Objects.equals(this.getThreadIdById(post.getParent()), post.getThread())) {
                        throw new DataIntegrityViolationException("thread exception");
                    }
                } catch (EmptyResultDataAccessException e) {
                    throw new DataIntegrityViolationException(e.getMessage());
                }
                preparedStatement.setLong(1, post.getId());
                preparedStatement.setString(2, post.getAuthor());
                preparedStatement.setString(3, post.getCreated());
                preparedStatement.setString(4, post.getForum());
                preparedStatement.setBoolean(5, post.getIsEdited());
                preparedStatement.setString(6, post.getMessage());
                preparedStatement.setObject(7, post.getParent());
                preparedStatement.setObject(8, post.getParent());
                preparedStatement.setLong(9, post.getId());
                preparedStatement.setLong(10, post.getThread());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            template.update("UPDATE forums SET posts = posts + ? " +
                    "WHERE lower(slug) = lower(?)", postsInfo.size(), thread.getForum());
        }
        return postsInfo;
    }


    public List<Post> getPostsWithFlatSort(ThreadModel thread, Integer limit, Integer offset, boolean desc) {

        final String sortParameter = (desc) ? "DESC" : "ASC";

        final String query =
                "SELECT " +
                "p.id, u.nickname as author, f.slug as forum, p.created, p.message, p.thread_id, p.parent, p.is_edited " +
                "FROM posts p " +
                "JOIN users u ON p.author_id = u.id " +
                "JOIN forums f ON p.forum_id = f.id " +
                "WHERE p.thread_id = ? " +
                "ORDER BY p.id " + sortParameter +
               " LIMIT ? OFFSET ? ";

        return template.query(query, POST_MAP, thread.getId(), limit, offset);
    }

    public List<Post> getPostsWithTreeSort(ThreadModel thread, Integer limit, Integer offset, boolean desc) {

        final String sortParameter = (desc) ? "DESC" : "ASC";

        final String query =
                "SELECT " +
                "p.id, u.nickname as author, f.slug as forum, p.created, p.message, p.thread_id, p.parent, p.is_edited " +
                "FROM posts p " +
                "JOIN users u ON p.author_id = u.id " +
                "JOIN forums f ON p.forum_id = f.id " +
                "WHERE p.thread_id = ? " +
                "ORDER BY path " + sortParameter +
               " LIMIT ? OFFSET ? ";

        return template.query(query, POST_MAP, thread.getId(), limit, offset);
    }

    public List<Post> getPostsWithParentTreeSort(ThreadModel thread, Integer limit, Integer offset, boolean desc) {

        final String sortParameter = (desc) ? "DESC" : "ASC";

        final String query =
                "WITH sub AS (" +
                    "SELECT " +
                    " path " +
                    "FROM posts " +
                "WHERE parent IS NULL AND thread_id = ? " +
                "ORDER BY path " + sortParameter +
               " LIMIT ? OFFSET ?" +
                ')' +
                "SELECT " +
                "p.id, u.nickname as author, f.slug as forum, p.created, p.message, p.thread_id, p.parent, p.is_edited " +
                "FROM posts p " +
                "JOIN users u ON p.author_id = u.id " +
                "JOIN forums f ON p.forum_id = f.id " +
                "JOIN sub ON sub.path <@ p.path " +
                "ORDER BY p.path " + sortParameter;

        return template.query(query, POST_MAP, thread.getId(), limit, offset);
    }


    public Post findPostById(Long id) {
        return template.queryForObject("SELECT p.id, f.slug as forum, u.nickname as author, p.message, p.thread_id, " +
                " p.parent, p.created, p.is_edited  " +
                "FROM posts p " +
                "JOIN users u ON p.author_id = u.id " +
                "JOIN forums f ON p.forum_id = f.id " +
                "WHERE p.id = ? ", POST_MAP, id);
    }

    public Post updatePost(Post postToUpdate, Post postInfo) {
        postToUpdate.setEdited(postInfo.getMessage() != null && !postToUpdate.getMessage().equals(postInfo.getMessage()));
        postToUpdate.setMessage(postInfo.getMessage() != null ? postInfo.getMessage() : postToUpdate.getMessage());
        template.update("UPDATE posts SET " +
                "message = ?," +
                "is_edited = ? " +
                "WHERE id = ?", postToUpdate.getMessage(), postToUpdate.getIsEdited(), postToUpdate.getId());

        return postToUpdate;
    }

    public Long getThreadIdById(Long id) {
        return template.queryForObject("SELECT thread_id FROM posts WHERE id = ?",Long.class, id);
    }
}
