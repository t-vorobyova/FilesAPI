package com.restfileapi.entries;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by Tatyana on 15.03.2017.
 */
public class Error {

    public enum Type {
        FILE_NOT_FOUND,
        FILE_DELETE_ERROR,
        FILE_IO_ERROR,
        INVALID_PARAMETER
    }

    private String id;
    private String summary;

    public static Error getError(Type type) {
        switch (type) {
            case FILE_NOT_FOUND:
                return new Error("FileNotFound", "File not found");
            case FILE_DELETE_ERROR:
                return new Error("FileDeleteError","File delete error");
            case FILE_IO_ERROR:
                return new Error("FileIOError","File IO error");
            case INVALID_PARAMETER:
                return new Error("InvalidParameter", "Invalid parameter or Illegal symbol in filename");
            default:
                return null;
        }
    }

    public Error() {
    }

    private Error(String id, String summary) {
        this.id = id;
        this.summary = summary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("summary", summary);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
