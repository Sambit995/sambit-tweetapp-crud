package com.tweetapp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.tweetapp.exception.TweetAppException;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.repo.TweetRepo;
import com.tweetapp.repo.UserRepo;
import com.tweetapp.request.TweetRequest;
import com.tweetapp.util.Envelope;
import com.tweetapp.util.TweetConstant;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TweetService {

	@Autowired
	TweetRepo tweetRepository;

	@Autowired
	UserRepo userRepository;

	Gson gson = new Gson();

	public ResponseEntity<Envelope<String>> postTweet(String userName, TweetRequest tweetRequest) {
		log.info(TweetConstant.IN_REQUEST_LOG, "postTweet", tweetRequest);
		Optional<User> findByEmailIdName = userRepository.findByEmailId(userName);
		long count = tweetRepository.count();
		log.info("total tweets " + count);
		if (!findByEmailIdName.isPresent())
			throw new TweetAppException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
					TweetConstant.USER_NAME_NOT_PRESENT);
		HashMap<String, List<String>> tweetReplies = new HashMap<>();
		String tweetRepliesJson = gson.toJson(tweetReplies);

		HashMap<String, Integer> tweetLikes = new HashMap<>();
		String likesJson = gson.toJson(tweetLikes);

		Tweet tweet = new Tweet(tweetRequest.getTweetId(), tweetRequest.getUserName(), tweetRequest.getTweet(),
				new Date(System.currentTimeMillis()), tweetRepliesJson, likesJson);
		tweetRepository.save(tweet);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "postTweet", tweet);
		return ResponseEntity.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, "Saved"));
	}

	public ResponseEntity<Envelope<List<Tweet>>> getAllTweet() {
		log.info(TweetConstant.IN_REQUEST_LOG, "getAllTweet", "getting All Tweets");
		List<Tweet> findAll = tweetRepository.findAll();
		if (findAll.isEmpty())
			throw new TweetAppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
					TweetConstant.NO_TWEETS_FOUND);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "getAllTweet", findAll);
		return ResponseEntity.ok(new Envelope<List<Tweet>>(HttpStatus.OK.value(), HttpStatus.OK, findAll));
	}

	public ResponseEntity<Envelope<List<Tweet>>> getAllUserTweet(String userName) {
		log.info(TweetConstant.IN_REQUEST_LOG, "getAllUserTweet", userName);
		List<Tweet> findByUserName = tweetRepository.findByUserName(userName);
		if (findByUserName.isEmpty())
			throw new TweetAppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
					TweetConstant.NO_TWEETS_FOUND);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "getAllUserTweet", findByUserName);
		return ResponseEntity.ok(new Envelope<List<Tweet>>(HttpStatus.OK.value(), HttpStatus.OK, findByUserName));
	}

	public ResponseEntity<Envelope<String>> updateTweet(String userName, int tweetId, TweetRequest tweetRequest) {
		log.info(TweetConstant.IN_REQUEST_LOG, "updateTweet", tweetRequest);
		tweetAndUserValidation(userName, tweetId);
		Tweet tweet = new Tweet(tweetRequest.getTweetId(), tweetRequest.getUserName(), tweetRequest.getTweet(),
				new Date(System.currentTimeMillis()), null, null);

		Optional<Tweet> existingTweet = tweetRepository.findById(tweetId);
		if (existingTweet.isPresent()) {

			Tweet updateTweet = existingTweet.get();
			updateTweet.setTweet(tweetRequest.getTweet());
			tweetRepository.save(updateTweet);
		}

		log.info(TweetConstant.EXITING_RESPONSE_LOG, "updateTweet", tweet);
		return ResponseEntity
				.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, TweetConstant.TWEET_UPDATED));
	}

	public ResponseEntity<Envelope<String>> deleteTweet(String userName, int tweetId) {
		log.info(TweetConstant.IN_REQUEST_LOG, "deleteTweet", tweetId);
		tweetAndUserValidation(userName, tweetId);
		tweetRepository.deleteById(tweetId);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "deleteTweet", TweetConstant.TWEET_DELETED);
		return ResponseEntity
				.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, TweetConstant.TWEET_DELETED));
	}

	public ResponseEntity<Envelope<String>> likeTweet(String userName, int tweetId) {

		log.info(TweetConstant.IN_REQUEST_LOG, "likeTweet", tweetId);

		try {

			tweetAndUserValidation(userName, tweetId);
			Optional<Tweet> findById = tweetRepository.findById(tweetId);
			HashMap<String, Integer> tweetLikes = gson.fromJson(findById.get().getLikes(), HashMap.class);

			if (tweetLikes.containsKey(userName)) {

				tweetLikes.put(userName, tweetLikes.get(userName) + 1);
			} else {
				tweetLikes.put(userName, 1);
			}

			String tweetRepliesJson = gson.toJson(tweetLikes);
			findById.get().setLikes(tweetRepliesJson);
			tweetRepository.save(findById.get());

		} catch (Exception e) {

			throw new TweetAppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
					"Error While Liking");
		}

		return ResponseEntity.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, TweetConstant.LIKED_TWEET));
	}

	public ResponseEntity<Envelope<String>> replyTweet(String userName, int tweetId, String reply) {

		log.info(TweetConstant.IN_REQUEST_LOG, "replyTweet", tweetId + " " + reply);

		try {

			tweetAndUserValidation(userName, tweetId);
			Optional<Tweet> findById = tweetRepository.findById(tweetId);

			if (findById.isPresent()) {

				Tweet tweet = findById.get();
				HashMap<String, List<String>> replies = gson.fromJson(tweet.getReplies(), HashMap.class);

				if (replies.containsKey(userName)) {

					List<String> list = new ArrayList<>(replies.get(userName));
					list.add(reply);

					replies.put(userName, list);
				} else {

					List<String> list = new ArrayList<>();
					list.add(reply);

					replies.put(userName, list);
				}

				String tweetRepliesJson = gson.toJson(replies);
				tweet.setReplies(tweetRepliesJson);
				tweetRepository.save(tweet);
			}

		} catch (Exception e) {

			throw new TweetAppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
					"Error While replying");
		}

		return ResponseEntity
				.ok(new Envelope<String>(HttpStatus.OK.value(), HttpStatus.OK, TweetConstant.REPLIED_TO_TWEET));
	}

	private void tweetAndUserValidation(String userName, int tweetId) {
		log.info(TweetConstant.IN_REQUEST_LOG, "tweetAndUserValidation :: Validating User", userName);
		Optional<Tweet> findById = tweetRepository.findById(tweetId);
		Optional<User> findByEmailIdName = userRepository.findByEmailId(userName);
		if (!findByEmailIdName.isPresent())
			throw new TweetAppException(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
					TweetConstant.USER_NAME_NOT_PRESENT);
		log.info(TweetConstant.IN_REQUEST_LOG, "tweetAndUserValidation :: Validating Tweet", tweetId);
		if (!findById.isPresent())
			throw new TweetAppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR,
					TweetConstant.NO_TWEETS_FOUND);
		log.info(TweetConstant.EXITING_RESPONSE_LOG, "tweetAndUserValidation", "User And Tweet Validated");
	}

}
