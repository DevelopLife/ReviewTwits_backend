package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.annotation.common.DateFormat;
import com.developlife.reviewtwits.message.annotation.project.ChartPeriod;
import com.developlife.reviewtwits.message.annotation.project.ProjectName;
import com.developlife.reviewtwits.message.request.StatMessageRequest;
import com.developlife.reviewtwits.message.response.statistics.DailyVisitInfoResponse;
import com.developlife.reviewtwits.message.response.project.RecentVisitInfoResponse;
import com.developlife.reviewtwits.message.response.statistics.VisitTotalGraphResponse;
import com.developlife.reviewtwits.message.response.project.*;
import com.developlife.reviewtwits.message.response.statistics.SaveStatResponse;
import com.developlife.reviewtwits.message.response.statistics.SimpleProjectInfoResponse;
import com.developlife.reviewtwits.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Validated
public class StatController {

    private final StatService statService;

    @PostMapping("/visited-info")
    public SaveStatResponse saveVisitedInfo(@AuthenticationPrincipal User user,
                                            @RequestBody @Valid StatMessageRequest statMessageRequest){

        return statService.saveStatInfo(user,statMessageRequest);
    }

    @GetMapping("/visit-graph-infos")
    public VisitTotalGraphResponse getVisitGraphInfos(@AuthenticationPrincipal User user,
                                                      @RequestParam
                                                      @ProjectName String projectName,
                                                      @RequestParam @ChartPeriod String range,
                                                      @RequestParam @ChartPeriod String interval,
                                                      @RequestParam(required = false) @DateFormat String endDate){

        return statService.getVisitGraphInfos(projectName, range, interval, user, endDate);
    }
    @GetMapping("/daily-visit-graph-infos")
    public DailyVisitInfoResponse getDailyVisitInfos(@AuthenticationPrincipal User user,
                                                     @RequestParam
                                                     @ProjectName String projectName,
                                                     @RequestParam @ChartPeriod String range){
        return statService.getDailyVisitInfos(projectName, range, user);
    }
    @GetMapping("/recent-visit-counts")
    public RecentVisitInfoResponse getRecentVisitCounts(@AuthenticationPrincipal User user,
                                                        @RequestParam @ProjectName String projectName
                                                        ){
        return statService.getRecentVisitCounts(projectName, user);
    }

    @GetMapping("/dashboard/simple-project-info")
    public SimpleProjectInfoResponse dashBoardSimpleInfo(@AuthenticationPrincipal User user,
                                                         @RequestParam @ProjectName String projectName){
        return statService.getSimpleProjectInfo(projectName, user);
    }

    @GetMapping("/dashboard/product-statistics")
    public List<ProductStatisticsResponse> dashBoardProductStatisticsInfo(@AuthenticationPrincipal User user,
                                                                          @RequestParam @ProjectName String projectName){
        return statService.getProductStatisticsInfo(projectName, user);
    }

    @GetMapping("/request-inflow-infos")
    public SearchFlowResponse getRequestSearchFlowInfos(@AuthenticationPrincipal User user,
                                                    @RequestParam @ProjectName String projectName){
        return statService.getRequestSearchFlowInfos(projectName, user);
    }

    @GetMapping("/readtime-info")
    public Map<Integer, Long> getReadTimeInfo(@AuthenticationPrincipal User user,
                                              @RequestParam @ProjectName String projectName){
        return statService.getReadTimeInfo(projectName, user);
    }
}