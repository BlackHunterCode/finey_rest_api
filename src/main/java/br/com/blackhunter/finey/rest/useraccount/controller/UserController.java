package br.com.blackhunter.finey.rest.useraccount.controller;

import br.com.blackhunter.finey.rest.core.dto.ApiResponse;
import br.com.blackhunter.finey.rest.useraccount.dto.projections.UserInfoDataProjected;
import br.com.blackhunter.finey.rest.useraccount.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    private final UserAccountService userAccountService;

    public UserController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserInfoDataProjected>> getUserInfo() {
        return ResponseEntity.ok(
                new ApiResponse<UserInfoDataProjected>(
                        "success",
                        HttpStatus.OK.value(),
                        userAccountService.getUserInfoByAuthToken()
                )
        );
    }
}
