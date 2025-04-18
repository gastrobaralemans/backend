package com.gastrobar_alemans_backend.repository;

import com.gastrobar_alemans_backend.model.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {
}