package com.ns.repository;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.ns.domain.OauthAccessToken;
import com.ns.domain.OauthRefreshToken;

public class MongoTokenStore implements TokenStore {

	private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

	@Resource
	OauthAccessTokenRepository oauthAccessTokenRepository;

	@Resource
	OauthRefereshTokenRepository oauthRefereshTokenRepository;

	public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
		this.authenticationKeyGenerator = authenticationKeyGenerator;
	}

	@Override
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		return readAuthentication(token.getValue());
	}

	@Override
	public OAuth2Authentication readAuthentication(String token) {
		OAuth2Authentication authentication = null;
		try {
			OauthAccessToken oauthAccessToken = this.oauthAccessTokenRepository.findByTokenId(extractTokenKey(token));
			authentication = deserializeAuthentication(oauthAccessToken.getAuthentication());
		} catch (Exception e) {
			removeAccessToken(token);
		}
		return authentication;
	}

	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		String refreshToken = null;
		if (token.getRefreshToken() != null) {
			refreshToken = token.getRefreshToken().getValue();
		}

		if (readAccessToken(token.getValue()) != null) {
			removeAccessToken(token.getValue());
		}

		OauthAccessToken oauthAccessToken = new OauthAccessToken();
		oauthAccessToken.setTokenId(extractTokenKey(token.getValue()));
		oauthAccessToken.setToken(serializeAccessToken(token));
		oauthAccessToken.setAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
		oauthAccessToken.setUsernNme(authentication.isClientOnly() ? null : authentication.getName());
		oauthAccessToken.setClientId(authentication.getOAuth2Request().getClientId());
		oauthAccessToken.setAuthentication(serializeAuthentication(authentication));
		oauthAccessToken.setRefreshToken(extractTokenKey(refreshToken));
		this.oauthAccessTokenRepository.save(oauthAccessToken);

	}

	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		OAuth2AccessToken accessToken = null;
		try {
			OauthAccessToken oauthAccessToken = this.oauthAccessTokenRepository
					.findByTokenId(extractTokenKey(tokenValue));
			accessToken = deserializeAccessToken(oauthAccessToken.getToken());
		} catch (Exception e) {
			removeAccessToken(tokenValue);
		}
		return accessToken;

	}

	@Override
	public void removeAccessToken(OAuth2AccessToken token) {
		removeAccessToken(token.getValue());
	}

	public void removeAccessToken(String tokenValue) {
		this.oauthAccessTokenRepository.removeByTokenId(extractTokenKey(tokenValue));
	}

	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {		
		OauthRefreshToken oauthRefreshToken=new OauthRefreshToken();
		oauthRefreshToken.setTokenId(extractTokenKey(refreshToken.getValue()));
		oauthRefreshToken.setToken(serializeRefreshToken(refreshToken));
		oauthRefreshToken.setAuthentication(serializeAuthentication(authentication));
		this.oauthRefereshTokenRepository.save(oauthRefreshToken);

	}

	@Override
	public OAuth2RefreshToken readRefreshToken(String tokenValue) {
		OAuth2RefreshToken refreshToken = null;
		try {
			OauthRefreshToken oauthRefreshToken=this.oauthRefereshTokenRepository.findByTokenId(extractTokenKey(tokenValue));
			refreshToken = deserializeRefreshToken(oauthRefreshToken.getToken());
		}
		catch (Exception e) {
			removeRefreshToken(tokenValue);
		}
		return refreshToken;
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
		return readAuthenticationForRefreshToken(token.getValue());
	}

	public OAuth2Authentication readAuthenticationForRefreshToken(String value) {
		OAuth2Authentication authentication = null;
		try {
			OauthRefreshToken oauthRefreshToken=this.oauthRefereshTokenRepository.findByTokenId(extractTokenKey(value));
			authentication = deserializeAuthentication(oauthRefreshToken.getToken());					
		}
		catch (Exception e) {
			removeRefreshToken(value);
		}

		return authentication;
	}

	
	@Override
	public void removeRefreshToken(OAuth2RefreshToken token) {
		removeRefreshToken(token.getValue());
	}


	public void removeRefreshToken(String token) {
		this.oauthRefereshTokenRepository.delete(extractTokenKey(token));
	}
	
	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		removeAccessTokenUsingRefreshToken(refreshToken.getValue());

	}
	
	public void removeAccessTokenUsingRefreshToken(String refreshToken) {
		this.oauthAccessTokenRepository.removeByRefreshToken(extractTokenKey(refreshToken));
	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		OAuth2AccessToken accessToken = null;
		String key = authenticationKeyGenerator.extractKey(authentication);
		try {
			OauthAccessToken oauthAccessToken = this.oauthAccessTokenRepository.findByAuthenticationId(key);
			accessToken = deserializeAccessToken(oauthAccessToken.getToken());

		} catch (Exception e) {
			{
			}
		}

		if (accessToken != null
				&& !key.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
			removeAccessToken(accessToken.getValue());
			storeAccessToken(accessToken, authentication);
		}
		return accessToken;
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
		List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
		try {
			OauthAccessToken oauthAccessToken = this.oauthAccessTokenRepository.findByUsernNmeAndClientId(userName,
					clientId);
			accessTokens.add(deserializeAccessToken(oauthAccessToken.getToken()));
		} catch (Exception e) {
		}
		accessTokens = removeNulls(accessTokens);
		return accessTokens;
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
		List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();

		try {
			OauthAccessToken oauthAccessToken=this.oauthAccessTokenRepository.findByClientId(clientId);
			accessTokens.add(deserializeAccessToken(oauthAccessToken.getToken()));
		}
		catch (Exception e) {
		}
		accessTokens = removeNulls(accessTokens);

		return accessTokens;
	}

	private List<OAuth2AccessToken> removeNulls(List<OAuth2AccessToken> accessTokens) {
		List<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
		for (OAuth2AccessToken token : accessTokens) {
			if (token != null) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	protected String extractTokenKey(String value) {
		if (value == null) {
			return null;
		}
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		}

		try {
			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		}
	}

	protected byte[] serializeAccessToken(OAuth2AccessToken token) {
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
		return SerializationUtils.serialize(authentication);
	}

	protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
		return SerializationUtils.deserialize(authentication);
	}

}
