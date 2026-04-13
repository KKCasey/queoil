package com.queoil.Queoil.repository;

import com.queoil.Queoil.model.SongRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRequestRepository extends JpaRepository<SongRequest, Long> {
    List<SongRequest> findByMusicianIdAndStatusOrderByCreatedAtAsc(long musicianId, String status);
}