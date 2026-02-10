package com.example.walkservice.safety.repository;

import com.example.walkservice.safety.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
