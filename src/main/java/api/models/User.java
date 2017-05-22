package api.models;

import api.models.generic.Model;

/**
 * Created by Vileven on 22.05.17.
 */
public class User extends Model<Long> {
    @Override
    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public String getAbout() {
        return about;
    }

    private  Long id;
    private String nickname;
    private String email;
    private String fullname;
    private String about;


    public User(Long id, String nickname, String email, String fullname, String about) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.fullname = fullname;
        this.about = about;
    }

    public User(String nickname, String email, String fullname, String about) {
        this.nickname = nickname;
        this.email = email;
        this.fullname = fullname;
        this.about = about;
    }
}
