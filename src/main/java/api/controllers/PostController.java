package api.controllers;

import api.models.Post;
import api.repositories.PostRepository;
import api.utils.PostResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Vileven on 25.05.17.
 */
@RestController
@RequestMapping(path = "/api/post")
public class PostController {
    private final PostRepository postRepository;

    @Autowired
    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        try {
            final Post post = postRepository.findPostById(id);
            return ResponseEntity.ok(new PostResponseBody(post));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }

    }

    @PostMapping("/{id}/details")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody Post postInfo) {
        try {
            final Post updatedPost = postRepository.updatePost(postRepository.findPostById(id), postInfo);
            return ResponseEntity.ok(updatedPost);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }
}
