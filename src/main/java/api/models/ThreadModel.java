package api.models;

import api.models.generic.Model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vileven on 22.05.17.
 */
public class ThreadModel extends Model<Long> {
    private Long id;
    private String slug;
    private String author;
    private String created;
    private String forum;
    private String message;
    private String title;

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    private Integer votes;

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthor(String author) {

        this.author = author;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public ThreadModel(Long id, String slug, String author, String created, String forum, String message, String title, Integer votes) {
        this.id = id;
        this.slug = slug;
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.message = message;
        this.title = title;
        this.votes = votes;

    }

    @JsonCreator
    public ThreadModel(@JsonProperty("message") String message, @JsonProperty("slug") String slug,
                       @JsonProperty("title") String title, @JsonProperty("author") String author,
                       @JsonProperty("created") String created, @JsonProperty("forum") String forum,
                       @JsonProperty(value = "votes",required = false) Integer votes) {
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.votes = votes;
    }


    @Override
    public Long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreated() {
        return created;
    }

    public String getForum() {
        return forum;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public Integer getVotes() {
        return votes;
    }

    public String getSlug() {
        return slug;
    }
}
