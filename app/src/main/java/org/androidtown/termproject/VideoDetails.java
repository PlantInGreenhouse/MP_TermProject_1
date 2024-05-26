package org.androidtown.termproject;

public class VideoDetails {
    public String title;
    public String category;
    public String description;
    public String url;

    public VideoDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(VideoDetails.class)
    }

    public VideoDetails(String title, String category, String description, String url) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }
}