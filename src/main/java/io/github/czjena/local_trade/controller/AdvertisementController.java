package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.dto.AdvertisementUpdateDto;
import io.github.czjena.local_trade.mappers.AdvertisementMapperToAdvertisementUpdateDto;
import io.github.czjena.local_trade.model.Advertisement;


import io.github.czjena.local_trade.model.Users;
import io.github.czjena.local_trade.repository.UsersRepository;
import io.github.czjena.local_trade.service.AdvertisementService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/advertisements")
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    public AdvertisementController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add")
    public ResponseEntity<Advertisement> createAdd(@RequestBody AdvertisementDto ad, @AuthenticationPrincipal UserDetails userDetails) {
        Advertisement created = advertisementService.addAd(ad,userDetails);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get advertisement by advertisement id")
    public ResponseEntity<Advertisement> getAdd(@PathVariable Integer id) {
        Advertisement advertisement = advertisementService.getAdvertisementById(id);
        return ResponseEntity.ok(advertisement);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update advertisement by advertisement id and user")
    public ResponseEntity<AdvertisementUpdateDto> updateAdd(@PathVariable Integer id, @RequestBody AdvertisementUpdateDto ad, @AuthenticationPrincipal UserDetails userDetails) {
        AdvertisementUpdateDto updated = advertisementService.changeAdvertisement(ad, userDetails, id);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')or @advertisementSecurityService.isOwner(authentication,id)")
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete advertisement by advertisement id and user")
    public ResponseEntity<Void> deleteAdd(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        advertisementService.deleteAdvertisement(userDetails, id);
        return ResponseEntity.ok().build();
    }
}
