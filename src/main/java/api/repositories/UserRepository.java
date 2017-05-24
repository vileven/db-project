package api.repositories;

import api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Vileven on 22.05.17.
 */
@Repository
@Transactional
public class UserRepository {
    private final JdbcTemplate template;

    @Autowired
    public UserRepository(JdbcTemplate template) {
        this.template = template;
    }

    public static final RowMapper<User> USER_MAP = (rs, rowNum) -> new User(rs.getLong("id"),
            rs.getString("nickname"), rs.getString("fullname"),
            rs.getString("email"), rs.getString("about"));

    public User createUser(String nickname, String fullname, String email, String about) {
        final User newUser = new User(nickname, fullname, email, about);
        template.update("INSERT INTO users (nickname, fullname, email, about) VALUES (?, ?, ?, ?)",
                newUser.getNickname(), newUser.getFullname(), newUser.getEmail(), newUser.getAbout());
        return newUser;
    }

    public List<User> findUsersByLoginOrEmail(String nickname, String email) {
        final String query =
                "SELECT * " +
                "FROM users AS u " +
                "WHERE lower(u.nickname) = lower(?) OR lower(u.email) = lower(?) ";
        return template.query(query, USER_MAP, nickname, email);
    }

    public User findUserByLogin(String nickname) {
        final String query = "SELECT * FROM users u WHERE lower(u.nickname) = lower(?) ";
        return template.queryForObject(query, USER_MAP, nickname);
    }

    public String getUserNicknameByNickname(String nickname) {
        return template.queryForObject("SELECT u.nickname FROM users u WHERE lower(nickname) = lower(?)",
                String.class, nickname);
    }

    public User updateUser(String nickname, User userInfo) {
        final String query = "UPDATE users SET " +
                                "fullname = COALESCE(?, fullname)," +
                                "email = COALESCE(?, email)," +
                                "about = COALESCE(?, about) " +
                              "WHERE lower(nickname) = lower(?) " +
                              "RETURNING * ";
        return template.queryForObject(query, USER_MAP, userInfo.getFullname(),
                userInfo.getEmail(), userInfo.getAbout(), nickname);
    }
}
