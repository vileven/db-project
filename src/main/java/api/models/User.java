package api.models;

import api.models.generic.Model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

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


    public User(Long id, String nickname, String fullname, String email, String about) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.fullname = fullname;
        this.about = about;
    }

    @JsonCreator
    public User(@JsonProperty("nickname") @Nullable String nickname, @JsonProperty("fullname") String fullname,
                @JsonProperty("email") String email, @JsonProperty("about") String about) {
        this.nickname = nickname;
        this.email = email;
        this.fullname = fullname;
        this.about = about;
    }
}
