package com.ns.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import com.ns.domain.OauthAccessToken;

public interface OauthAccessTokenRepository extends MongoRepository<OauthAccessToken, String> {

	String removeByTokenId(@Param("tokenId") String tokenId);

	String removeByRefreshToken(@Param("refreshToken") String refreshToken);

	OauthAccessToken findByTokenId(@Param("tokenId") String tokenId);
	
	OauthAccessToken findByClientId(@Param("clientId") String clientId);

	OauthAccessToken findByAuthenticationId(@Param("authenticationId") String authenticationId);

	OauthAccessToken findByUsernNmeAndClientId(@Param("usernNme") String usernNme,@Param("clientId") String clientId);

}
