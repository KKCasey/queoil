package com.queoil.Queoil.controller;

import com.queoil.Queoil.model.Setlist;
import com.queoil.Queoil.model.Song;
import com.queoil.Queoil.repository.SetlistRepository;
import com.queoil.Queoil.repository.SongRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongRepository songRepository;
    private final SetlistRepository setlistRepository;

    public SongController(SongRepository songRepository, SetlistRepository setlistRepository) {
        this.songRepository = songRepository;
        this.setlistRepository = setlistRepository;
    }

    @GetMapping("/create")
    public Song createSong() {
        Setlist setlist = setlistRepository.findById(1L).orElseThrow();

        Song song = new Song();
        song.setTitle("Wonderwall");
        song.setSetlist(setlist);

        return songRepository.save(song);
    }
}