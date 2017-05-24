package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Vileven on 24.05.17.
 */
public class Vote {
    public String getNickname() {
        return nickname;
    }

    public Long getThread() {
        return thread;
    }

    public Integer getVoice() {
        return voice;
    }

    private String nickname;
    private Long thread;
    private Integer voice;

    public Vote(String nickname, Long thread, Integer voice) {
        this.nickname = nickname;
        this.thread = thread;
        this.voice = voice;
    }

    @JsonCreator
    public Vote(@JsonProperty("nickname") String nickname, @JsonProperty("voice") Integer voice) {
        this.nickname = nickname;
        this.voice = voice;
    }
}
