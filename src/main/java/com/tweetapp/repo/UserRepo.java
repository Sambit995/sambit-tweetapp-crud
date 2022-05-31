package com.tweetapp.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tweetapp.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
	
	Optional<User> findByEmailIdAndPassword(String emailId, String password);

	Optional<User> findByEmailId(String userName);
}
