package dev.skamdem.patentsinvestor.data;

import dev.skamdem.patentsinvestor.models.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by kamdem
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
}
