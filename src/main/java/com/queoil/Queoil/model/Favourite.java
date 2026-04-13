package com.queoil.Queoil.model;

import jakarta.persistence.*;

@Entity
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User listener;

    @ManyToOne
    private User musician;

    // Getters & Setters
    public Long getId() { return id; }

    public User getListener() { return listener; }
    public void setListener(User listener) { this.listener = listener; }

    public User getMusician() { return musician; }
    public void setMusician(User musician) { this.musician = musician; }
}
