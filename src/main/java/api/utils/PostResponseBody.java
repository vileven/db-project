package api.utils;

import api.models.Forum;
import api.models.Post;
import api.models.ThreadModel;
import api.models.User;

/**
 * Created by Vileven on 25.05.17.
 */
public class PostResponseBody {
    private Post post;
    private User author;
    private Forum forum;
    private ThreadModel thread;

    public void setPost(Post post) {
        this.post = post;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public void setThread(ThreadModel thread) {
        this.thread = thread;
    }

    public User getAuthor() {

        return author;
    }

    public Forum getForum() {
        return forum;
    }

    public ThreadModel getThread() {
        return thread;
    }

    public PostResponseBody(Post post, User author, Forum forum, ThreadModel thread) {
        this.post = post;
        this.author = author;
        this.forum = forum;
        this.thread = thread;

    }

    public PostResponseBody() {

    }

    public Post getPost() {
        return post;
    }
}
