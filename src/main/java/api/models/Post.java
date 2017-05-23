package api.models;

import api.models.generic.Model;

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
    private String created;
    private String forum;
    private String message;
    private Long thread;
    private Long parent;
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
}
