package dev.skamdem.patentsinvestor.data;

import dev.skamdem.patentsinvestor.models.Stock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by kamdem
 */
@Repository
public interface StockRepository extends CrudRepository<Stock, Integer> {
}
