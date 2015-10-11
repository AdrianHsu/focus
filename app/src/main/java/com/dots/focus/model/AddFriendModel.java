package com.dots.focus.model;

/**
 * Created by Adrian Hsu on 2015/10/10.
 */
public class AddFriendModel {

    private String username;
    private String avatar;
    private String absoluteTime;
    private String post;
    private String reason;
    private String location;

    private int likes;
    private int comments;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAbsoluteTime(){ return absoluteTime; }

    public void setAbsoluteTime(String time) { this.absoluteTime = time; }

    public String getReason() {
      return reason;
    }

    public void setReason(String reason) {
      this.reason = reason;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLikes() {
      return likes;
    }

    public void setLikes(int likes) {
      this.likes = likes;
    }

    public int getComments() {
      return comments;
    }

    public void setComments(int comments) {
      this.comments = comments;
    }
}
