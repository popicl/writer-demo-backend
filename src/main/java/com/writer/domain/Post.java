package com.writer.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.security.SecureRandomParameters;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_post")
    @SequenceGenerator(
            name = "seq_post",
            allocationSize = 1
    )
    private Long id;
    private String postName;
    private String content;
    private boolean isPrivate;
    @ManyToOne
    private User user;
    private LocalDateTime createdDate;

    public Post() {
    }

    public Long getId() {
        return id;
    }

    public Post setId(Long id) {
        this.id = id;
        return this;
    }

    public String getPostName() {
        return postName;
    }

    public Post setPostName(String postName) {
        this.postName = postName;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Post setContent(String content) {
        this.content = content;
        return this;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public User getUser() {
        return user;
    }

    public Post setUser(User user) {
        this.user = user;
        return this;
    }
}
