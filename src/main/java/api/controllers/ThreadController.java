package api.controllers;

import api.models.Post;
import api.models.ThreadModel;
import api.models.Vote;
import api.repositories.PostRepository;
import api.repositories.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        } /*catch () {

        }*/ catch (SQLException e) {
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
}
