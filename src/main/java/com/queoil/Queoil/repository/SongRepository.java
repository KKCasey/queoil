package com.queoil.Queoil.repository;

import com.queoil.Queoil.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findBySetlistId(long setlistId);
}