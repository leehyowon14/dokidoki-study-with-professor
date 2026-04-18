package com.animalleague.april.contract.support;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractSmokeController {

    @GetMapping("/contract-smoke")
    String smoke() {
        return "ok";
    }
}
