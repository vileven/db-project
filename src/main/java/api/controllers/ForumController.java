package api.controllers;

import api.models.Forum;
import api.models.ThreadModel;
import api.services.ForumService;
import api.services.ThreadService;
import api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Vileven on 22.05.17.
 */
@RestController
@RequestMapping(path = "/api/forum")
public class ForumController {

    private final ForumService forumService;
    private final UserService userService;
    private final ThreadService threadService;

    @Autowired
    public ForumController(ForumService forumService, UserService userService, ThreadService threadService) {
        this.forumService = forumService;
        this.userService = userService;
        this.threadService = threadService;
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

    @PostMapping("/{slug}/create")
    public ResponseEntity<?> createThreadByForumSlug(@RequestBody ThreadModel threadInfo) {
        try {
            final ThreadModel createdThread = threadService.createThread(threadInfo);
            createdThread.setAuthor(userService.getUserNicknameByNickname(createdThread.getAuthor()));
            createdThread.setForum(forumService.getSlugBySlug(createdThread.getForum()));

            return ResponseEntity.status(HttpStatus.CREATED).body(createdThread);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadService.findThreadBySlug(threadInfo.getSlug()));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }


    @GetMapping("/{slug}/threads")
    public ResponseEntity<?> getThreadsByForumSlug(@PathVariable String slug,
                                                   @RequestParam(value = "desc", defaultValue = "false") boolean desc,
                                                   @RequestParam(value = "since", required = false) String since,
                                                   @RequestParam(value = "limit", defaultValue = "100") Integer limit) {
        try {
            final Long forumId = forumService.getIdBySlug(slug);
            final List<ThreadModel> threads = threadService.getForumThreads(forumId, limit, since, desc);

            return ResponseEntity.ok(threads);
        } catch (EmptyResultDataAccessException e ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }
}
