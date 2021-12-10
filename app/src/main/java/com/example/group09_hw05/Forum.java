package com.example.group09_hw05;

import java.io.Serializable;
import java.util.Map;

public class Forum implements Serializable {
    private String forumId;
    private String title;
    private String desc;
    private String createdByUid;
    private String createdByName;
    private String createdDate;
    private String likes;
    private Map<String,Object> likedBy;

    public Forum() {
    }

    public Forum(String forumId, String title, String desc, String createdByUid, String createdByName, String createdDate, String likes, Map<String,Object> likedBy) {
        this.forumId = forumId;
        this.title = title;
        this.desc = desc;
        this.createdByUid = createdByUid;
        this.createdByName = createdByName;
        this.createdDate = createdDate;
        this.likes = likes;
        this.likedBy = likedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreatedByUid() {
        return createdByUid;
    }

    public void setCreatedByUid(String createdByUid) {
        this.createdByUid = createdByUid;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public Map<String, Object> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Map<String, Object> likedBy) {
        this.likedBy = likedBy;
    }
}
