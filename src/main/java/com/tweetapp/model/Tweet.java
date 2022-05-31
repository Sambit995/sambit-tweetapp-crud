package com.tweetapp.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Entity(name = "TWEET")
public class Tweet {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer tweetId;
	
	@Column(name = "USER_NAME")
	private String userName;
	
	@Column(name = "TWEET")
	private String tweet;
	
	@Column(name = "CREATED")
	private Date created;
	
	@Column(name = "LIKES")
	private String likes;
	
	@Column(name = "REPLIES")
	private String replies;
}
