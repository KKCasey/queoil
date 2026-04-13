package com.queoil.Queoil.repository;

import com.queoil.Queoil.model.Setlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetlistRepository extends JpaRepository<Setlist, Long> {
    List<Setlist> findByUserId(long userId);
}
