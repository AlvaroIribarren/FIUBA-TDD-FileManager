package fiuba.tecnicas.plugins.extras.utils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;

@Entity
@Table(name = "users")
public class User {
	@Id
	@NotNull
	@JsonIgnore
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@NotNull
	@Column(name = "email", unique = true)
	private String email;
	
	@Column(name = "refreshToken")
	@JsonIgnore
	private String refreshToken;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
