package com.my.mobilesafe.domain;

/**
 * Created by MY on 2016/10/5.
 */

public class UpdateInfo {
    private String version;
    private String url;
    private String description;

    @Override
    public String toString() {
        return "UpdateInfo [version=" + version + ",url=" + url
                + ",description=" + description + "]";
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
