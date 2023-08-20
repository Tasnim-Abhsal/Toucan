package com.example.toucan;

public class MusicClass {
    private String uri;
    private String thumbUri;
    private String name;

    public MusicClass(String uri, String thumbUri, String name) {
        this.uri = uri;
        this.thumbUri = thumbUri;
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getThumbUri() {
        return thumbUri;
    }

    public void setThumbUri(String thumbUri) {
        this.thumbUri = thumbUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
