package com.animalleague.april.contract.support;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractSmokeController {

    @GetMapping("/contract-smoke")
    String smoke() {
        return "ok";
    }

    @GetMapping("/contract-smoke/header")
    String smokeWithHeader(@RequestHeader("X-Contract-Token") String token) {
        return token;
    }

    @PostMapping(path = "/contract-smoke", consumes = MediaType.APPLICATION_JSON_VALUE)
    String smokeWithJson(@RequestBody String payload) {
        return payload;
    }
}
