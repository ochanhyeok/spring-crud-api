package hello.crud.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import hello.crud.auth.dto.LoginRequest;
import hello.crud.common.LoginFailedException;
import hello.crud.member.Member;
import hello.crud.member.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	private static final String LOGIN_FAILED_MESSAGE = "아이디 또는 비밀번호가 올바르지 않습니다";

	public LoginMember login(LoginRequest request) {
		Member member = memberRepository.findByLoginId(request.getLoginId())
			.orElseThrow(() -> new LoginFailedException(LOGIN_FAILED_MESSAGE));

		if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new LoginFailedException(LOGIN_FAILED_MESSAGE);
		}

		return new LoginMember(member.getId());
	}
}
