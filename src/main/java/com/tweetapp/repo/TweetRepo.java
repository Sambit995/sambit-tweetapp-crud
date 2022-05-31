package com.tweetapp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tweetapp.model.Tweet;

@Repository
public interface TweetRepo extends JpaRepository<Tweet, Integer> {

	List<Tweet> findByUserName(String userName);
	
}
