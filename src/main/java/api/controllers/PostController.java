package api.controllers;

import api.models.Post;
import api.repositories.ForumRepository;
import api.repositories.PostRepository;
import api.repositories.ThreadRepository;
import api.repositories.UserRepository;
import api.utils.PostResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Vileven on 25.05.17.
 */
@RestController
@RequestMapping(path = "/api/post")
public class PostController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;
    private final ForumRepository forumRepository;

    @Autowired
    public PostController(PostRepository postRepository, UserRepository userRepository, ThreadRepository threadRepository, ForumRepository forumRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.threadRepository = threadRepository;
        this.forumRepository = forumRepository;
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<?> getPost(@PathVariable Long id,
                                     @RequestParam(value = "related", required = false) List<String> related) {
        try {
            final Post post = postRepository.findPostById(id);
            final PostResponseBody response =  new PostResponseBody();
            response.setPost(post);
            if (related != null) {
                if (related.contains("user")) {
                    response.setAuthor(userRepository.findUserByLogin(post.getAuthor()));
                }
                if (related.contains("forum")) {
                    response.setForum(forumRepository.findForumBySlug(post.getForum()));
                }
                if (related.contains("thread")) {
                    response.setThread(threadRepository.findThreadById(post.getThread()));
                }
            }
            return ResponseEntity.ok(response);
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
