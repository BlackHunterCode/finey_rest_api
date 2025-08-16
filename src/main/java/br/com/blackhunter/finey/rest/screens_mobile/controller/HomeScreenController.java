package br.com.blackhunter.finey.rest.screens_mobile.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/screens-mobile/home")
public class HomeScreenController {
    @GetMapping("/analysis")
    public void getHomeScreenAnalysisFromReferenceDate()
    {

    }
}
