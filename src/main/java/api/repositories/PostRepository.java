package api.repositories;

import api.models.Post;
import api.models.ThreadModel;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

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

    public static final RowMapper<Post> POST_MAP = (rs, rowNum) -> new Post(rs.getLong("id"), rs.getString("auth"),
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
                preparedStatement.setLong(1, post.getId());
                preparedStatement.setString(2, post.getAuthor());
                preparedStatement.setString(3, post.getCreated());
                preparedStatement.setString(4, post.getForum());
                preparedStatement.setBoolean(5, post.getEdited());
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
}