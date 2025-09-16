package br.com.blackhunter.finey.rest.finance.analysis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/finance/analysis")
public class AnalysisController {

    @GetMapping("/current-balance-projection")
    public void currentBalanceProjection() {

    }
}
