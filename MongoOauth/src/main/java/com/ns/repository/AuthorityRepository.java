package com.ns.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.ns.domain.Authority;

public interface AuthorityRepository extends MongoRepository<Authority, String> {
}
