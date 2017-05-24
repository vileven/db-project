package api.utils;

import api.models.Post;

import java.util.List;

/**
 * Created by Vileven on 25.05.17.
 */
public class PostsGetWithSortionResponseBody {
    private String marker;
    private List<Post> posts;

    public PostsGetWithSortionResponseBody(String marker, List<Post> posts) {
        this.marker = marker;
        this.posts = posts;
    }

    public String getMarker() {
        return marker;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
