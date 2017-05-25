package api.controllers;

import api.models.Post;
import api.models.ThreadModel;
import api.models.Vote;
import api.repositories.PostRepository;
import api.repositories.ThreadRepository;
import api.utils.PostsGetWithSortionResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Vileven on 23.05.17.
 */
@RestController
@RequestMapping(path = "/api/thread")
public class ThreadController {
    private final ThreadRepository threadRepository;
    private final PostRepository postRepository;

    @Autowired
    public ThreadController(ThreadRepository threadRepository, PostRepository postRepository) {
        this.threadRepository = threadRepository;
        this.postRepository = postRepository;
    }


    @PostMapping("/{slug_or_id}/create")
    public ResponseEntity<?> createPosts(@PathVariable(name = "slug_or_id") String slugOrId, @RequestBody List<Post> postsInfo) {
        try {
            final ThreadModel thread = (slugOrId.matches("\\d+")) ? threadRepository.findThreadById(Long.parseLong(slugOrId)) :
                    threadRepository.findThreadBySlug(slugOrId);

            final List<Post> createdPosts = postRepository.createBatch(postsInfo, thread);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPosts);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("");
        } catch (EmptyResultDataAccessException | SQLException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    @PostMapping("/{slug_or_id}/vote")
    public ResponseEntity<?> voteThread(@PathVariable(name = "slug_or_id") String slugOrId, @RequestBody Vote voteInfo) {
        try {
            final ThreadModel thread = (slugOrId.matches("\\d+")) ? threadRepository.findThreadById(Long.parseLong(slugOrId)) :
                    threadRepository.findThreadBySlug(slugOrId);

            return ResponseEntity.ok(threadRepository.addVote(voteInfo, thread));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    @GetMapping("/{slug_or_id}/details")
    public ResponseEntity<?> getThread(@PathVariable(name = "slug_or_id") String slugOrId) {
        try {
            final ThreadModel thread = (slugOrId.matches("\\d+")) ? threadRepository.findThreadById(Long.parseLong(slugOrId)) :
                    threadRepository.findThreadBySlug(slugOrId);
            return ResponseEntity.ok(thread);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    @GetMapping("{slug_or_id}/posts")
    public ResponseEntity<?> getThreadPosts(@PathVariable(name = "slug_or_id") String slugOrId,
                                            @RequestParam(value = "limit", defaultValue = "100") Integer limit,
                                            @RequestParam(value = "marker", defaultValue = "0") String mark,
                                            @RequestParam(value = "sort", defaultValue = "flat") String sort,
                                            @RequestParam(value = "desc", defaultValue = "false") boolean desc) {

        try {
            final ThreadModel thread = (slugOrId.matches("\\d+")) ? threadRepository.findThreadById(Long.parseLong(slugOrId)) :
                    threadRepository.findThreadBySlug(slugOrId);

            Integer offset = Integer.valueOf(mark);
            List<Post> posts = null;
            switch (sort) {
                case "flat":
                    posts = postRepository.getPostsWithFlatSort(thread, limit, offset, desc);
                    offset += posts.size();
                    break;

                case "tree":
                    posts = postRepository.getPostsWithTreeSort(thread, limit, offset, desc);
                    offset += posts.size();
                    break;

                case "parent_tree":
                    posts = postRepository.getPostsWithParentTreeSort(thread, limit, offset, desc);
                    offset += Math.min(limit, posts.size());
                    break;
            }

            return ResponseEntity.ok(new PostsGetWithSortionResponseBody(offset.toString(), posts));

        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }

    }

    @PostMapping("/{slug_or_id}/details")
    public ResponseEntity<?> getThreadDetails(@PathVariable(name = "slug_or_id") String slugOrId,
                                              @RequestBody ThreadModel threadInfo) {
        try {
            final ThreadModel thread = (slugOrId.matches("\\d+")) ? threadRepository.findThreadById(Long.parseLong(slugOrId)) :
                    threadRepository.findThreadBySlug(slugOrId);
            final ThreadModel updatedThread = threadRepository.updateThread(thread, threadInfo);
            return ResponseEntity.ok(updatedThread);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }
}
