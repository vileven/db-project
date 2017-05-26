package api.repositories;

import api.models.ThreadModel;
import api.models.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Vileven on 22.05.17.
 */
@Transactional
@Repository
public class ThreadRepository {
    private final JdbcTemplate template;

    public static final RowMapper<ThreadModel> THREAD_MAP = (rs, rowNum) -> new ThreadModel(rs.getLong("id"),
            rs.getString("slug"), rs.getString("author"),
            LocalDateTime.ofInstant(rs.getTimestamp("created").toInstant(), ZoneOffset.ofHours(0))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
            rs.getString("forum"), rs.getString("message"), rs.getString("title"),
            rs.getInt("votes"));

    @Autowired
    public ThreadRepository(JdbcTemplate template) {
        this.template = template;
    }


    public ThreadModel createThread(ThreadModel threadInfo) {
        final String query = "INSERT INTO threads (author, created, forum, message, slug, title) " +
                                "VALUES ((SELECT u.nickname FROM users u WHERE lower(u.nickname) = lower(?)), " +
                                    "COALESCE(?::TIMESTAMPTZ, current_timestamp), " +
                                    "( SELECT f.slug FROM forums f WHERE lower(f.slug) = lower(?) ), ?, ?, ?) RETURNING *";

        final ThreadModel updatedThread = template.queryForObject(query, THREAD_MAP, threadInfo.getAuthor(), threadInfo.getCreated(),
                threadInfo.getForum(), threadInfo.getMessage(), threadInfo.getSlug(), threadInfo.getTitle());


        template.update("UPDATE forums SET threads = threads + 1 WHERE lower(slug) = lower(?)",
                threadInfo.getForum());

        return updatedThread;
    }

    public ThreadModel findThreadBySlug(String slug) {
        return template.queryForObject(
                "SELECT t.id, t.author, t.forum," +
                        " t.slug, t.created, t.message, t.title, t.votes FROM " +
                "threads t " +
                "WHERE lower(t.slug) = lower(?)", THREAD_MAP, slug);
    }

    public List<ThreadModel> getForumThreads(String slug, Integer limit, String since, boolean desc) {
        final StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT t.id, t.slug, t.author, ")
                .append(" t.forum, t.created, t.message, t.title, t.votes ")
                .append("FROM ")
                .append("threads t ")
                .append("WHERE lower(t.forum) = lower(?) ");

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

        return template.query(queryBuilder.toString(), THREAD_MAP, slug, limit);
    }

    public Long findThreadIdBySlug(String slug) {
        return template.queryForObject("SELECT id FROM threads WHERE lower(slug) = lower(?) ", Long.class, slug);
    }

    public ThreadModel findThreadById(Long id) {
        final String query = "" +
                "SELECT t.id, t.slug, t.author, t.created, t.forum, t.message, t.title, t.votes " +
                "FROM " +
                "   threads t " +
                "WHERE t.id = ?";

        return template.queryForObject(query, THREAD_MAP, id);
    }

    public ThreadModel addVote(Vote voteInfo, ThreadModel thread) {
        template.update("INSERT INTO votes (user_id, thread_id, voice) VALUES " +
                "((SELECT u.id FROM users u WHERE lower(nickname) = lower(?)), ?, ?) " +
                "ON CONFLICT (user_id, thread_id) DO " +
                " UPDATE SET voice = ?",
                voteInfo.getNickname(), thread.getId(), voteInfo.getVoice(), voteInfo.getVoice()
        );

        thread.setVotes(template.queryForObject("SELECT t.votes FROM threads t " +
                "WHERE t.id = ?", Integer.class, thread.getId()));

        return thread;
    }

    public ThreadModel updateThread(ThreadModel oldThread, ThreadModel threadInfo) {

        oldThread.setMessage(   threadInfo.getMessage() == null ? oldThread.getMessage() :
                threadInfo.getMessage() );
        oldThread.setTitle( threadInfo.getTitle() == null ? oldThread.getTitle() :
                threadInfo.getTitle() );

        template.update("UPDATE threads " +
                "SET " +
                "message = ?," +
                "title = ? " +
                "WHERE id = ? ", oldThread.getMessage(), oldThread.getTitle(), oldThread.getId());

        return oldThread;
    }

}
