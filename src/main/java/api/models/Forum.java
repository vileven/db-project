package api.models;

import api.models.generic.Model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vileven on 22.05.17.
 */
public class Forum extends Model<Long> {

    private Long id;
    private String title;

    public void setUser(String user) {
        this.user = user;
    }

    @JsonProperty("user")
    private String user;

    private String slug;
    private Integer posts;
    private Integer threads;

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }

    public String getSlug() {
        return slug;
    }

    public Integer getPosts() {
        return posts;
    }

    public Integer getThreads() {
        return threads;
    }

    public Forum(Long id, String title, String user, String slug, Integer posts, Integer threads) {
        this.id = id;
        this.title = title;
        this.user = user;
        this.slug = slug;

        this.posts = posts;
        this.threads = threads;
    }

    @JsonCreator
    public Forum(@JsonProperty("slug") String slug, @JsonProperty("title") String title, @JsonProperty("user") String user) {
        this.slug = slug;
        this.title = title;
        this.user = user;
    }

    @Override
    public Long getId() {
        return id;
    }
}
