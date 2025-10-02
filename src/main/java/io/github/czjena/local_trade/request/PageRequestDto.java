package io.github.czjena.local_trade.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PageRequestDto(
        Integer page,
        Integer size,
        String sortBy,
        String sortDirection
) {
    public Pageable toPageable() {
        Sort sort = Sort.unsorted();
        if (sortBy != null && sortDirection != null) {
            sort = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
        }
        return PageRequest.of(page != null ? page : 0,
                size != null ? size : 10,
                sort);
    }
}
