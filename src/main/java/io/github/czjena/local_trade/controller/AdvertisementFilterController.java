package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.request.AdvertisementPageRequestDto;
import io.github.czjena.local_trade.service.AdvertisementFilterService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController("/advertisement/search")
public class AdvertisementFilterController {
    private final AdvertisementFilterService advertisementFilterService;

    public AdvertisementFilterController(AdvertisementFilterService advertisementFilterService) {
        this.advertisementFilterService = advertisementFilterService;
    }
    @PostMapping("/filter")
    @Operation(summary = "Filter advertisements", description = "Sortowanie po polu \", allowableValues = {\"PRICE\",\"TITLE\",\"CREATED_AT} , Kierunek Sortowania SortDirection allowableValues = ASC,DESC")

    public ResponseEntity<Page<AdvertisementDto>> filterAndPageAdvertisements(@RequestBody AdvertisementFilterDto filterDto, @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(advertisementFilterService.filterAndPageAdvertisements(filterDto, pageable));
    }
}
