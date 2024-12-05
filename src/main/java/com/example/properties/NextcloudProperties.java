package com.example.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nextcloud")
public class NextcloudProperties {
    private String url;
    private String username;
    private String password;
    private Excel excel = new Excel();

    public static class Excel {
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Excel getExcel() {
        return excel;
    }

    public void setExcel(Excel excel) {
        this.excel = excel;
    }
}
