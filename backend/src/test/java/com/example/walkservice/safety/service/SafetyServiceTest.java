package com.example.walkservice.safety.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.walkservice.safety.dto.BlockResponse;
import com.example.walkservice.safety.dto.CreateBlockRequest;
import com.example.walkservice.safety.dto.CreateReportRequest;
import com.example.walkservice.safety.dto.ReportResponse;
import com.example.walkservice.safety.entity.Block;
import com.example.walkservice.safety.entity.Report;
import com.example.walkservice.safety.repository.BlockRepository;
import com.example.walkservice.safety.repository.ReportRepository;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SafetyServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private BlockRepository blockRepository;

    @InjectMocks
    private SafetyService safetyService;

    @Test
    void createReport_savesAndMapsResponse() throws Exception {
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            setId(report, 99L);
            return report;
        });

        ReportResponse response = safetyService.createReport(10L, new CreateReportRequest(20L, null, "reason"));

        assertThat(response.getId()).isEqualTo(99L);
        assertThat(response.getReporterUserId()).isEqualTo(10L);
        assertThat(response.getReportedUserId()).isEqualTo(20L);
        assertThat(response.getMeetupId()).isNull();
        assertThat(response.getReason()).isEqualTo("reason");
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    void createBlock_savesAndMapsResponse() throws Exception {
        when(blockRepository.save(any(Block.class))).thenAnswer(invocation -> {
            Block block = invocation.getArgument(0);
            setId(block, 99L);
            return block;
        });

        BlockResponse response = safetyService.createBlock(10L, new CreateBlockRequest(20L));

        assertThat(response.getId()).isEqualTo(99L);
        assertThat(response.getBlockerUserId()).isEqualTo(10L);
        assertThat(response.getBlockedUserId()).isEqualTo(20L);
        assertThat(response.getCreatedAt()).isNotNull();
    }

    private static void setId(Object entity, Long id) throws Exception {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
