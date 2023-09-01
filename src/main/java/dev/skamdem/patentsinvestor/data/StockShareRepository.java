package dev.skamdem.patentsinvestor.data;

import dev.skamdem.patentsinvestor.models.StockShare;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by kamdem
 */
@Repository
public interface StockShareRepository extends CrudRepository<StockShare, Integer> {
}
