package com.ocam.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Hiker {

    @Id
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String login;

    public Hiker() {

    }

    @Generated(hash = 414962445)
    public Hiker(Long id, @NotNull String email, @NotNull String login) {
        this.id = id;
        this.email = email;
        this.login = login;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
