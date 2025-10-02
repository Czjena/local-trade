package io.github.czjena.local_trade.request;

import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.enums.AdvertisementSortField;
import io.github.czjena.local_trade.enums.SortDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AdvertisementPageRequestDto {
    private int page = 0;
    private int size = 10;
    private List<AdvertisementSortField> sortBy = List.of(AdvertisementSortField.CREATED_AT);
    private SortDirection sortDirection = SortDirection.DESC;
    private AdvertisementFilterDto filter;

}
