package br.com.blackhunter.finey.rest.screens_mobile.controller;

import br.com.blackhunter.finey.rest.core.dto.ApiResponse;
import br.com.blackhunter.finey.rest.finance.analysis.dto.payload.AnalysisPayload;
import br.com.blackhunter.finey.rest.screens_mobile.dto.HomeScreenAnalysisData;
import br.com.blackhunter.finey.rest.screens_mobile.service.HomeScreenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/screens-mobile/home")
public class HomeScreenController {
    private final HomeScreenService homeScreenService;

    public HomeScreenController(HomeScreenService homeScreenService) {
        this.homeScreenService = homeScreenService;
    }

    @PostMapping("/analysis")
    public ResponseEntity<ApiResponse<HomeScreenAnalysisData>> getHomeScreenAnalysisFromReferenceDate(
            @RequestBody AnalysisPayload payload
    ) {
        return ResponseEntity.ok(ApiResponse.<HomeScreenAnalysisData>builder()
                .status("success")
                .data(homeScreenService.getHomeScreenAnalysisFromReferenceDate(
                        payload.getBankAccountIds(),
                        payload.getReferenceDateMonthYear(),
                        payload.getStartDate(),
                        payload.getEndDate()
                ))
                .build());
    }
}
