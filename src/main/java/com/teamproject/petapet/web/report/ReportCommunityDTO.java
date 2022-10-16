package com.teamproject.petapet.web.report;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportCommunityDTO {
    private Long reportId;
    private String reportReason;
    private Long communityId;
}
