package com.example.walkservice.safety.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.security.BlockedWriteGuard;
import com.example.walkservice.safety.dto.BlockResponse;
import com.example.walkservice.safety.dto.CreateBlockRequest;
import com.example.walkservice.safety.dto.CreateReportRequest;
import com.example.walkservice.safety.dto.ReportResponse;
import com.example.walkservice.safety.entity.Block;
import com.example.walkservice.safety.entity.Report;
import com.example.walkservice.safety.repository.BlockRepository;
import com.example.walkservice.safety.repository.ReportRepository;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SafetyService {

    private final ReportRepository reportRepository;
    private final BlockRepository blockRepository;
    private final BlockedWriteGuard blockedWriteGuard;

    public SafetyService(
            ReportRepository reportRepository,
            BlockRepository blockRepository,
            BlockedWriteGuard blockedWriteGuard
    ) {
        this.reportRepository = reportRepository;
        this.blockRepository = blockRepository;
        this.blockedWriteGuard = blockedWriteGuard;
    }

    public ReportResponse createReport(Long actorUserId, CreateReportRequest request) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "SAFETY_REPORT_CREATE_FORBIDDEN");

        Report report = new Report(
                actorUserId,
                request.getReportedUserId(),
                request.getMeetupId(),
                request.getReason(),
                OffsetDateTime.now()
        );

        Report saved = reportRepository.save(report);
        return toResponse(saved);
    }

    public BlockResponse createBlock(Long actorUserId, CreateBlockRequest request) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "SAFETY_BLOCK_CREATE_FORBIDDEN");

        Block block = new Block(actorUserId, request.getBlockedUserId(), OffsetDateTime.now());
        Block saved = blockRepository.save(block);
        return toResponse(saved);
    }

    private ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getReporterUserId(),
                report.getReportedUserId(),
                report.getMeetupId(),
                report.getReason(),
                report.getCreatedAt()
        );
    }

    private BlockResponse toResponse(Block block) {
        return new BlockResponse(
                block.getId(),
                block.getBlockerUserId(),
                block.getBlockedUserId(),
                block.getCreatedAt()
        );
    }
}
