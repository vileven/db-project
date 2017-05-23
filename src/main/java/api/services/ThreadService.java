package api.services;

import api.models.ThreadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Vileven on 22.05.17.
 */
@Transactional
@Service
public class ThreadService {
    private final JdbcTemplate template;

    public static final RowMapper<ThreadModel> THREAD_MAP = (rs, rowNum) -> new ThreadModel(rs.getLong("id"),
            rs.getString("slug"), rs.getString("author"),
            LocalDateTime.ofInstant(rs.getTimestamp("created").toInstant(), ZoneOffset.ofHours(0))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
            rs.getString("forum"), rs.getString("message"), rs.getString("title"),
            rs.getInt("votes"));

    @Autowired
    public ThreadService(JdbcTemplate template) {
        this.template = template;
    }


    public ThreadModel createThread(ThreadModel threadInfo) {
//        final String created = threadInfo.getCreated();
//        Timestamp timestamp = null;
//        if (created != null) {
//            final String zonedTime = ZonedDateTime.parse(threadInfo.getCreated()).format(DateTimeFormatter.ISO_INSTANT);
//            timestamp = new Timestamp(ZonedDateTime.parse(zonedTime).toLocalDateTime().toInstant(ZoneOffset.UTC).toEpochMilli());
//        }
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

    public List<ThreadModel> getForumThreads(Long forumId, Integer limit, String since, boolean desc) {
        final StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT t.id, t.slug, u.nickname as author, ")
                .append(" f.slug as forum, t.created, t.message, t.title, t.votes ")
                .append("FROM ")
                .append("threads t ")
                .append("JOIN users u ON t.author_id = u.id ")
                .append("JOIN forums f ON t.forum_id = f.id ")
                .append("WHERE t.forum_id = ? ");

        if (since != null) {
            if (desc) {
                queryBuilder.append("AND t.created <= '").append(since).append("'::TIMESTAMPTZ ")
                        .append(" ORDER BY t.created DESC ");
            } else {
                queryBuilder.append("AND t.created >= '").append(since).append("'::TIMESTAMPTZ ")
                        .append(" ORDER BY t.created ASC ");
            }
        } else {
            queryBuilder.append("ORDER BY t.created ");
            if (desc) {
                queryBuilder.append("DESC ");
            } else {
                queryBuilder.append("ASC ");
            }
        }

        queryBuilder.append("LIMIT ?");

        return template.query(queryBuilder.toString(), THREAD_MAP, forumId, limit);
    }
}
