package com.queoil.Queoil.controller;

import com.queoil.Queoil.model.Setlist;
import com.queoil.Queoil.model.User;
import com.queoil.Queoil.repository.SetlistRepository;
import com.queoil.Queoil.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setlists")
public class SetlistController {

    private final SetlistRepository setlistRepository;
    private final UserRepository userRepository;

    public SetlistController(SetlistRepository setlistRepository, UserRepository userRepository) {
        this.setlistRepository = setlistRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/create")
    public Setlist createSetlist() {
        User user = userRepository.findById(1L).orElseThrow();

        Setlist setlist = new Setlist();
        setlist.setTitle("Test Gig Stelist");
        setlist.setUser(user);

        return setlistRepository.save(setlist);
    }
}