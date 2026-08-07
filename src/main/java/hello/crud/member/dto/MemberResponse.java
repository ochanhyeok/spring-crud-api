package hello.crud.member.dto;

import hello.crud.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

	private Long id;
	private String loginId;
	private String name;

	public static MemberResponse of(Member member) {
		return MemberResponse.builder()
			.id(member.getId())
			.loginId(member.getLoginId())
			.name(member.getName())
			.build();
	}
}
