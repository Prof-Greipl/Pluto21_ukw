package de.hawlandshut.pluto21_ukw.model;

import com.google.firebase.database.DataSnapshot;

public class Post {
    public String uid;
    public String author;
    public String title;
    public String body;
    public long timestamp;

    public Post(String uid, String author, String title, String body, long timestamp) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
    }

    public static Post fromSnapShot(DataSnapshot snapShot){
        String uid = (String) snapShot.child("uid").getValue();
        String title = (String) snapShot.child("title").getValue();
        String body = (String) snapShot.child("body").getValue();
        String author = (String) snapShot.child("author").getValue();

        Post p = new Post(uid, author, title, body, 0 );
        return p;
    }
}
