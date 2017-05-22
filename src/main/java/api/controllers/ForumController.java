package api.controllers;

import api.models.Forum;
import api.services.ForumService;
import api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Vileven on 22.05.17.
 */
@RestController
@RequestMapping(path = "/api/forum")
public class ForumController {

    private final ForumService forumService;
    private final UserService userService;

    @Autowired
    public ForumController(ForumService forumService, UserService userService) {
        this.forumService = forumService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createForum(@RequestBody Forum forumInfo) {
        try {
            forumService.createForum(forumInfo);
            forumInfo.setUser(userService.getUserNicknameByNickname(forumInfo.getUser()));
            return ResponseEntity.status(HttpStatus.CREATED).body(forumInfo);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumService.findForumBySlug(forumInfo.getSlug()));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    @GetMapping("/{slug}/details")
    public ResponseEntity<?> getForum(@PathVariable String slug) {
        try {
            final Forum findedForum = forumService.findForumBySlug(slug);
            return ResponseEntity.ok(findedForum);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }
}
