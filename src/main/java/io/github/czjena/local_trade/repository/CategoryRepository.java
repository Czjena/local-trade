package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Object> findByName(String name);
}
