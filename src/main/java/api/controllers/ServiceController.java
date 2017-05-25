package api.controllers;

import api.utils.DBInfoResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Vileven on 25.05.17.
 */
@RestController
@RequestMapping(path = "/api/service")
public class ServiceController {
    private final JdbcTemplate template;

    @Autowired
    public ServiceController(JdbcTemplate template) {
        this.template = template;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getDbInfo() {
        final DBInfoResponseBody response = template.queryForObject("SELECT " +
                "  (SELECT count(*) FROM forums)  AS forum, " +
                "  (SELECT count(*) FROM posts)   AS post, " +
                "  (SELECT count(*) FROM threads) AS thread, " +
                "  (SELECT count(*) FROM users)   AS \"user\" ",  (rs, rowNum) -> new DBInfoResponseBody(
                        rs.getInt("forum"), rs.getInt("post"), rs.getInt("thread"),
                rs.getInt("user")
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearDataBase() {
        template.update("TRUNCATE TABLE posts CASCADE ");
        template.update("TRUNCATE TABLE threads CASCADE ");
        template.update("TRUNCATE TABLE forums CASCADE ");
        template.update("TRUNCATE TABLE users CASCADE ");
        return ResponseEntity.ok("");
    }

}
