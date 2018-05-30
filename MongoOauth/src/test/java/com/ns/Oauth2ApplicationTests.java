package com.ns;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ns.Oauth2Application;
import com.ns.domain.Authority;
import com.ns.domain.User;
import com.ns.repository.AuthorityRepository;
import com.ns.repository.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Oauth2Application.class)
@WebAppConfiguration
public class Oauth2ApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Resource
	UserRepository repository;
	@Resource
	AuthorityRepository authorityRepository;

	// INSERT INTO user (username,email, password, activated) VALUES ('admin',
	// 'admin@mail.me',
	// 'b8f57d6d6ec0a60dfe2e20182d4615b12e321cad9e2979e0b9f81e0d6eda78ad9b6dcfe53e4e22d1',
	// true);

	@Test
	public void saveUser() {

		User user = new User();
		repository.deleteAll();
		authorityRepository.deleteAll();
		//user = repository.findByUsernameEqualsIgnoreCase("admin");
		//if (user.getUsername()==null) {
		Set<Authority>  authorities=new HashSet<>();
		Authority authority = new Authority();
		authority.setName("ROLE_USER");
		authorities.add(authority);
			user.setUsername("admin");
			user.setEmail("admin@mail.me");
			user.setPassword("b8f57d6d6ec0a60dfe2e20182d4615b12e321cad9e2979e0b9f81e0d6eda78ad9b6dcfe53e4e22d1");
			user.setActivated(true);
			user.setAuthorities(authorities);
			repository.save(user);
			System.out.println("save user :"+repository.findAll());
			System.out.println("authority:"+authorityRepository.findAll());
		//}
	}

/*	@Test
	public void saveAuthority() {
		Authority authority = new Authority();
		authority.setName("ROLE_USER");
		authorityRepository.save(authority);
		System.out.println("authority:"+authorityRepository.findAll());
	}*/

}
