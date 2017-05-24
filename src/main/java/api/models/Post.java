package api.models;

import api.models.generic.Model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vileven on 23.05.17.
 */
public class Post extends Model<Long> {
    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setThread(Long thread) {
        this.thread = thread;
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

    public Long getThread() {
        return thread;
    }

    public Long getParent() {
        return parent;
    }

    public Boolean getEdited() {
        return isEdited;
    }

    private Long id;
    private String author;

    public void setCreated(String created) {
        this.created = created;
    }

    private String created;
    private String forum;
    private String message;
    private Long thread;
    private Long parent = null;
    private Boolean isEdited;

    public Post(Long id, String author, String created, String forum, String message, Long thread, Long parent, Boolean isEdited) {
        this.id = id;
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.message = message;
        this.thread = thread;
        this.parent = parent;
        this.isEdited = isEdited;
    }

    @JsonCreator
    public Post(@JsonProperty("author") String author, @JsonProperty("message") String message,
                @JsonProperty(value = "isEdited") boolean isEdited) {
        this.author = author;
        this.message = message;
        this.isEdited = isEdited;
    }
}
