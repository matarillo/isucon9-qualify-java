package isucon9.qualify.data;

import org.springframework.data.repository.CrudRepository;

import isucon9.qualify.dto.UserToRegister;

public interface UserToRegisterRepository extends CrudRepository<UserToRegister, Long> {
    
}
