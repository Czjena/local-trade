package io.github.czjena.local_trade.controller;

import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.response.ResponseAdvertisementDto;
import io.github.czjena.local_trade.service.AdvertisementFilterService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/advertisements")
public class AdvertisementFilterController {
    private final AdvertisementFilterService advertisementFilterService;

    public AdvertisementFilterController(AdvertisementFilterService advertisementFilterService) {
        this.advertisementFilterService = advertisementFilterService;
    }
    @GetMapping("/search")
    @Operation(summary = "Filter advertisements", description = "Sortowanie po polu \", allowableValues = {\"PRICE\",\"TITLE\",\"CREATED_AT} , Kierunek Sortowania SortDirection allowableValues = ASC,DESC")
    public ResponseEntity<Page<ResponseAdvertisementDto>> filterAndPageAdvertisements(
            @RequestParam (required = false) Integer categoryId ,
            @RequestParam(required = false)BigDecimal minPrice,
            @RequestParam(required = false)BigDecimal maxPrice,
            @RequestParam(required = false,name= "title") String titleFilter,
            @RequestParam(required = false)String location,
            @RequestParam(required = false)Boolean active, @PageableDefault Pageable pageable) {
        AdvertisementFilterDto filterDto = new AdvertisementFilterDto(categoryId,minPrice,maxPrice,location,titleFilter,active);
        return ResponseEntity.ok(advertisementFilterService.filterAndPageAdvertisements(filterDto, pageable));
    }
}
