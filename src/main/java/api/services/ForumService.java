package api.services;

import api.models.Forum;
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
public class ForumService {
    private final JdbcTemplate template;

    public static final RowMapper<Forum> FORUM_MAP = (rs, rowNum) -> new Forum(rs.getLong("id"),
            rs.getString("title"), rs.getString("user"),rs.getString("slug"),
            rs.getInt("posts"), rs.getInt("threads"));

    @Autowired
    public ForumService(JdbcTemplate template) {
        this.template = template;
    }


    public void createForum(Forum forumInfo) {
        template.update("INSERT INTO forums (slug, title, user_id) VALUES " +
                                "(?, ?, (SELECT id FROM users WHERE lower(nickname) = lower(?)))",
                forumInfo.getSlug(), forumInfo.getTitle(), forumInfo.getUser());
    }

    public Forum findForumBySlug(String slug) {
        return template.queryForObject("SELECT f.id, f.title, u.nickname as \"user\", f.slug, f.posts, f.threads " +
                "FROM " +
                "forums f " +
                "JOIN users u ON f.user_id = u.id " +
                " WHERE lower(f.slug) = lower(?) ", FORUM_MAP, slug);
    }

    public String getSlugBySlug(String slug) {
        return template.queryForObject("SELECT f.slug FROM forums f WHERE lower(f.slug) = lower(?)",
                String.class, slug);
    }

    public Long getIdBySlug(String slug) {
        return template.queryForObject("SELECT f.id FROM forums f WHERE lower(f.slug) = lower(?)",
                Long.class, slug);
    }
}
