package com.ns.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import com.ns.domain.User;

public interface UserRepository extends MongoRepository<User, String> {
	User findByUsernameEqualsIgnoreCase(@Param("username") String username);

	User findByEmail(String email);

	User findByEmailAndActivationKey(String email, String activationKey);

	User findByEmailAndResetPasswordKey(String email, String resetPasswordKey);

}