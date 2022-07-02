package isucon9.qualify.data;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import isucon9.qualify.dto.User;

public interface UserRepository extends CrudRepository<User, Long> {

    public Optional<User> findByAccountName(String accountName);

}
