package com.queoil.Queoil.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String role;
    private boolean liveNow;

    @ManyToOne
    @JoinColumn(name = "active_setlist_id")
    private Setlist activeSetlist;
    
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Setlist> setlists;

    public User() {
    }

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Setlist> getSetlists() {
    return setlists;
    }

    public void setSetlists(List<Setlist> setlists) {
    this.setlists = setlists;
    }

    public boolean isLiveNow() {
        return liveNow;
    }

    public void setLiveNow(boolean liveNow) {
        this.liveNow = liveNow;
    }

    public Setlist getActiveSetlist() {
        return activeSetlist;
    }

    public void setActiveSetlist(Setlist activeSetlist) {
        this.activeSetlist = activeSetlist;
    }
}
