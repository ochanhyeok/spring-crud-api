package hello.crud.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	private Long id;
	private String loginId;
	private String name;
	private String password;

	public Member(String loginId, String name, String password) {
		this.loginId = loginId;
		this.name = name;
		this.password = password;
	}

	public void assignId(Long id) {
		this.id = id;
	}
}
