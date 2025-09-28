package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.model.Advertisement;


import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.AdvertisementService;

import io.github.czjena.local_trade.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController("/advertisement")
public class AdvertisementController {


    private final AdvertisementService advertisementService;
    private final AuthenticationService authenticationService;
    private final UsersRepository usersRepository;

    public AdvertisementController(AdvertisementService advertisementService, AuthenticationService authenticationService, UsersRepository usersRepository) {
        this.advertisementService = advertisementService;
        this.authenticationService = authenticationService;
        this.usersRepository = usersRepository;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public ResponseEntity<Advertisement> createAdd(@RequestBody AdvertisementDto ad, @AuthenticationPrincipal UserDetails userDetails) {
        Users user = usersRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Advertisement created = advertisementService.addAd(ad,user);
        return ResponseEntity.ok(created);
    }
    @GetMapping("/get/{id}")
    @Operation(summary = "Get advertisement by advertisement id")
    public ResponseEntity<Advertisement> getAdd(@PathVariable Integer id) {
        Advertisement advertisement = advertisementService.getAdvertisementById(id);
        return ResponseEntity.ok(advertisement);
    }

}
