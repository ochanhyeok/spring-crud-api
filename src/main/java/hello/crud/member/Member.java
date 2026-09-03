package hello.crud.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50)
	private String loginId;

	@Column(length = 50)
	private String name;

	@Column(length = 100)
	private String password;

	@Builder
	public Member(String loginId, String name, String password) {
		this.loginId = loginId;
		this.name = name;
		this.password = password;
	}

}
