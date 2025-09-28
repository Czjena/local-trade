package io.github.czjena.local_trade.repository;

import io.github.czjena.local_trade.model.Advertisement;
import io.github.czjena.local_trade.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
