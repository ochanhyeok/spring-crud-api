package hello.crud.member.dto;

import lombok.Data;

@Data
public class MemberCreateRequest {

	private String loginId;
	private String name;
	private String password;
}
