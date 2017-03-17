package com.restfileapi.entries;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by Tatyana on 14.03.2017.
 */
public class Metadata {
    private String path;
    private long size;
    private long modified;
    private String hash;

    public Metadata() {

    }

    public Metadata(String path, long size, long modified, String hash) {
        this.path = path;
        this.size = size;
        this.modified = modified;
        this.hash = hash;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("path", path);
            jsonObject.put("size", size);
            jsonObject.put("modified", modified);
            jsonObject.put("hash", hash);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metadata metadata = (Metadata) o;

        if (size != metadata.size) return false;
        if (modified != metadata.modified) return false;
        if (!path.equals(metadata.path)) return false;
        return hash.equals(metadata.hash);
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (int) (modified ^ (modified >>> 32));
        result = 31 * result + hash.hashCode();
        return result;
    }
}
