package com.tweetapp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
@Entity(name = "USER")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer userId;

	@NotBlank(message = "firstName cannot be null")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Invalid firstName")
	@Column(name = "FIRST_NAME")
	public String firstName;

	@NotBlank(message = "lastName cannot be null")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "Invalid LastName")
	@Column(name = "LAST_NAME")
	public String lastName;

	@Pattern(regexp = "male|female", message = "Invalid Gender")
	@NotBlank(message = "gender cannot be null")
	@Column(name = "GENDER")
	public String gender;

	@Pattern(regexp = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$", message = "Date OF birth Should be in YYYY-MM-DD format")
	@Column(name = "DOB")
	public String dob;

	@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid EmailId")
	@NotBlank(message = "Enter Valid Email Id")
	@Column(name = "EMAIL_ID")
	public String emailId;

	@NotBlank(message = "password cannot be null")
	@Column(name = "PASSWORD")
	public String password;
}
