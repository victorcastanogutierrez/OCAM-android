package com.ocam.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class Hiker {

    @Unique
    private Long id;

    @Id
    private Long id_local;

    @NotNull
    private String email;

    @NotNull
    private String login;

    public Hiker() {

    }



    @Generated(hash = 257416339)
    public Hiker(Long id, Long id_local, @NotNull String email,
            @NotNull String login) {
        this.id = id;
        this.id_local = id_local;
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

    public Long getId_local() {
        return this.id_local;
    }

    public void setId_local(Long id_local) {
        this.id_local = id_local;
    }
}
