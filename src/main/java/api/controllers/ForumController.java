package api.controllers;

import api.models.Forum;
import api.models.ThreadModel;
import api.models.User;
import api.repositories.ForumRepository;
import api.repositories.ThreadRepository;
import api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
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

    private final ForumRepository forumRepository;
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;

    @Autowired
    public ForumController(ForumRepository forumRepository, UserRepository userRepository, ThreadRepository threadRepository) {
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
        this.threadRepository = threadRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createForum(@RequestBody Forum forumInfo) {
        try {
            forumRepository.createForum(forumInfo);
            forumInfo.setUser(userRepository.getUserNicknameByNickname(forumInfo.getUser()));
            return ResponseEntity.status(HttpStatus.CREATED).body(forumInfo);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumRepository.findForumBySlug(forumInfo.getSlug()));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    @GetMapping("/{slug}/details")
    public ResponseEntity<?> getForum(@PathVariable String slug) {
        try {
            final Forum findedForum = forumRepository.findForumBySlug(slug);
            return ResponseEntity.ok(findedForum);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    @PostMapping("/{slug}/create")
    public ResponseEntity<?> createThreadByForumSlug(@PathVariable String slug,
            @RequestBody ThreadModel threadInfo) {
        try {
            if(threadInfo.getForum() == null) {
                threadInfo.setForum(slug);
            }
            final ThreadModel createdThread = threadRepository.createThread(threadInfo);
//            createdThread.setAuthor(userRepository.getUserNicknameByNickname(createdThread.getAuthor()));
//            createdThread.setForum(forumRepository.getSlugBySlug(createdThread.getForum()));

            return ResponseEntity.status(HttpStatus.CREATED).body(createdThread);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(threadRepository.findThreadBySlug(threadInfo.getSlug()));
        } catch (EmptyResultDataAccessException | DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }


    @GetMapping("/{slug}/threads")
    public ResponseEntity<?> getThreadsByForumSlug(@PathVariable String slug,
                                                   @RequestParam(value = "desc", defaultValue = "false") boolean desc,
                                                   @RequestParam(value = "since", required = false) String since,
                                                   @RequestParam(value = "limit", defaultValue = "100") Integer limit) {
        try {
            final String slugs = forumRepository.getSlugBySlug(slug);
            final List<ThreadModel> threads = threadRepository.getForumThreads(slugs, limit, since, desc);

            return ResponseEntity.ok(threads);
        } catch (EmptyResultDataAccessException e ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    @GetMapping("/{slug}/users")
    public ResponseEntity<?> getUsersByForum(@PathVariable String slug,
                                             @RequestParam(value = "desc", defaultValue = "false") boolean desc,
                                             @RequestParam(value = "since", required = false) String since,
                                             @RequestParam(value = "limit", defaultValue = "100") Integer limit) {
        try {
            final Long forumId = forumRepository.getIdBySlug(slug);
            final List<User> users = userRepository.findForumMembers(forumId, limit, since, desc);

            return ResponseEntity.ok(users);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }
}
