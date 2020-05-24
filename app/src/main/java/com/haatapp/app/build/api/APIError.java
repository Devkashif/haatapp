package com.haatapp.app.build.api;

/**
 * Created by Tamil on 9/25/2017.
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class APIError {
    @SerializedName("type")
    @Expose
    private List<String> type = null;

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public APIError withType(List<String> type) {
        this.type = type;
        return this;
    }
}
