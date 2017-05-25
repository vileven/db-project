package api.utils;

/**
 * Created by Vileven on 25.05.17.
 */
public class DBInfoResponseBody {
    private final Integer forum;
    private final Integer post;
    private final Integer thread;
    private final Integer user;

    public Integer getForum() {
        return forum;
    }

    public Integer getPost() {
        return post;
    }

    public Integer getThread() {
        return thread;
    }

    public Integer getUser() {
        return user;
    }

    public DBInfoResponseBody(Integer forum, Integer post, Integer thread, Integer user) {
        this.forum = forum;
        this.post = post;

        this.thread = thread;
        this.user = user;
    }
}
