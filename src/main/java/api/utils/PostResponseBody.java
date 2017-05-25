package api.utils;

import api.models.Post;

/**
 * Created by Vileven on 25.05.17.
 */
public class PostResponseBody {
    private Post post;

    public PostResponseBody(Post post) {
        this.post = post;
    }

    public Post getPost() {
        return post;
    }
}
