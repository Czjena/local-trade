package io.github.czjena.local_trade.service;

import io.github.czjena.local_trade.dto.AdvertisementFilterDto;
import io.github.czjena.local_trade.mappers.AdvertisementDtoMapper;
import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.repository.AdvertisementRepository;
import io.github.czjena.local_trade.response.ResponseAdvertisementDto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class AdvertisementFilterServiceImpl implements AdvertisementFilterService {
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementDtoMapper advertisementDtoMapper;
    public AdvertisementFilterServiceImpl(AdvertisementRepository advertisementRepository, AdvertisementDtoMapper advertisementDtoMapper) {
        this.advertisementRepository = advertisementRepository;
        this.advertisementDtoMapper = advertisementDtoMapper;
    }
    @Override
    public Specification<Advertisement> getSpecification(AdvertisementFilterDto filter) {
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

    @Override
    @Transactional
    public Page<ResponseAdvertisementDto> filterAndPageAdvertisements(AdvertisementFilterDto advertisementFilterDto, Pageable pageable) {
        Specification<Advertisement> spec = getSpecification(advertisementFilterDto);
        Page<Advertisement> advertisements = advertisementRepository.findAll(spec,pageable);
        return advertisements.map(advertisementDtoMapper::toResponseAdvertisementDto);
    }
}
