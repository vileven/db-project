package api.services;

import api.models.ThreadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Vileven on 22.05.17.
 */
@Transactional
@Service
public class ThreadService {
    private final JdbcTemplate template;

    public static final RowMapper<ThreadModel> THREAD_MAP = (rs, rowNum) -> new ThreadModel(rs.getLong("id"),
            rs.getString("slug"), rs.getString("author"), rs.getString("created"),
            rs.getString("forum"), rs.getString("message"), rs.getString("title"),
            rs.getInt("votes"));

    @Autowired
    public ThreadService(JdbcTemplate template) {
        this.template = template;
    }


    public ThreadModel createThread(ThreadModel threadInfo) {
        final String query = "INSERT INTO threads (author_id, created, forum_id, message, slug, title) " +
                                "VALUES ((SELECT u.id FROM users u WHERE lower(u.nickname) = lower(?)), " +
                                    "COALESCE(?::TIMESTAMPTZ, current_timestamp), " +
                                    "(SELECT f.id FROM forums f WHERE lower(slug) = lower(?)), ?, ?, ?) RETURNING id";

        final long id = template.queryForObject(query, Long.class, threadInfo.getAuthor(), threadInfo.getCreated(),
                threadInfo.getForum(), threadInfo.getMessage(), threadInfo.getSlug(), threadInfo.getTitle());
        threadInfo.setId(id);

        return threadInfo;
    }

    public ThreadModel findThreadBySlug(String slug) {
        return template.queryForObject(
                "SELECT t.id, u.nickname as author, f.slug as forum," +
                        " t.slug, t.created, t.message, t.title, t.votes FROM " +
                "threads t " +
                "JOIN users u ON t.author_id = u.id " +
                "JOIN forums f ON t.forum_id = f.id  " +
                "WHERE lower(t.slug) = lower(?)", THREAD_MAP, slug);
    }
}
