package io.github.czjena.local_trade.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "hello ";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hello1")
    public String hello2() {
        return "hello2";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/hello2")
    public String hello3() {
        return "hello3";
    }

}
