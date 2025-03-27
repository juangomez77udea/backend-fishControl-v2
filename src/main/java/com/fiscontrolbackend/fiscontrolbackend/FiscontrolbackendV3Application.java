package com.fiscontrolbackend.fiscontrolbackend;

import com.fiscontrolbackend.fiscontrolbackend.models.user.ERole;
import com.fiscontrolbackend.fiscontrolbackend.models.user.RoleEntity;
import com.fiscontrolbackend.fiscontrolbackend.models.user.UserEntity;
import com.fiscontrolbackend.fiscontrolbackend.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class FiscontrolbackendV3Application {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(FiscontrolbackendV3Application.class, args);
	}

	@Bean
	CommandLineRunner init() {
		return args -> {
			if (userRepository.count() == 0) {
				createUser("admin@mail.com", "admin", ERole.ADMIN);
			}
		};
	}

	private void createUser(String email, String username, ERole role) {
		UserEntity userEntity = UserEntity.builder()
				.email(email)
				.username(username)
				.password(passwordEncoder.encode("123456"))
				.enabled(true)
				.roles(Set.of(RoleEntity.builder()
						.name(role)
						.build()))
				.build();

		userRepository.save(userEntity);
	}
}