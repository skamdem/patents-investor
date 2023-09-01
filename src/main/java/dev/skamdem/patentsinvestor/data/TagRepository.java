package dev.skamdem.patentsinvestor.data;

import dev.skamdem.patentsinvestor.models.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by kamdem
 */
@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {
}
