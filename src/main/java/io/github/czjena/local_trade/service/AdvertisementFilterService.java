package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.AdvertisementDto;
import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.enums.AdvertisementSortField;
import io.github.czjena.local_trade.enums.SortDirection;
import io.github.czjena.local_trade.mappers.AdvertisementMapperToAdvertisementDto;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
public class AdvertisementFilterService {
    private final AdvertisementRepository advertisementRepository;
    public AdvertisementFilterService(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }
    private Specification<Advertisement> getSpecification(AdvertisementFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

                if (filter.title() != null && !filter.title().isEmpty()) {
                    Predicate titlePredicate = cb.like(
                            cb.lower(root.get("title")),
                            "%" + filter.title().toLowerCase() + "%"
                    );

                    Predicate descriptionPredicate = cb.like(
                            cb.lower(root.get("description")),
                            "%" + filter.title().toLowerCase() + "%"
                    );
                    predicates.add(cb.or(titlePredicate, descriptionPredicate));
                }

                if (filter.categoryId() != null) {
                    predicates.add(cb.equal(root.get("category").get("id"), filter.categoryId()));
                }

                if (filter.minPrice() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.minPrice()));
                }

                if (filter.maxPrice() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.maxPrice()));
                }

                if (filter.location() != null && !filter.location().isEmpty()) {
                    predicates.add(cb.like(
                            cb.lower(root.get("location")),
                            "%" + filter.location().toLowerCase() + "%"
                    ));
                }

                if (filter.active() != null) {
                    predicates.add(cb.equal(root.get("active"), filter.active()));
                }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    public Page<AdvertisementDto> filterAndPageAdvertisements(AdvertisementFilterDto advertisementFilterDto,Pageable pageable) {
        Specification<Advertisement> spec = getSpecification(advertisementFilterDto);

        Sort.Direction direction = advertisementFilterDto.sortDirection() == SortDirection.ASC
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        List<AdvertisementSortField> fieldsToSort =
                (advertisementFilterDto.sortBy() != null && !advertisementFilterDto.sortBy().isEmpty())
                        ? advertisementFilterDto.sortBy()
                        : List.of(AdvertisementSortField.CREATED_AT);

        List<Sort.Order> orders = fieldsToSort.stream()
                    .map(field -> new Sort.Order(direction,
                            switch (field) {
                                case PRICE -> "price";
                                case TITLE -> "title";
                                case CREATED_AT -> "createdAt";
                            }))
                    .toList();


        Sort sort = Sort.by(orders);

        Pageable pageableWithSort = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<Advertisement> advertisements = advertisementRepository.findAll(spec,pageableWithSort);
        return advertisements.map(AdvertisementMapperToAdvertisementDto::toDto);
    }
}
