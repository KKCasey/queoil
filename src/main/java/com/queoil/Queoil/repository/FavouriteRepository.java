package com.queoil.Queoil.repository;

import com.queoil.Queoil.model.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    List<Favourite> findByListenerId(Long listenerId);

    Optional<Favourite> findByListenerIdAndMusicianId(Long listenerId, Long musicianId);
}
