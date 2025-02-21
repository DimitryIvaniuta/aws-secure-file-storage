package com.securefilestorage.repository;


import com.securefilestorage.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Find user by login.
     *
     * @param login value
     * @return optional user.
     */
    Optional<User> findByLogin(String login);

}
