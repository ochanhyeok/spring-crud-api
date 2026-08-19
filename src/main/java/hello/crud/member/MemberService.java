package hello.crud.member;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.member.dto.MemberResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public MemberResponse create(MemberCreateRequest request) {
		Member member = new Member(request.getLoginId(), request.getName(), passwordEncoder.encode(request.getPassword()));
		memberRepository.save(member);
		return MemberResponse.of(member);
	}

	public List<MemberResponse> findAll() {
		return memberRepository.findAll().stream()
			.map(MemberResponse::of)
			.toList();
	}

	public MemberResponse findOne(Long id) {
		return MemberResponse.of(findMemberById(id));

	}

	// Member 반환용 메서드
	public Member findMemberById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("회원이 없습니다. id=" + id));
	}
}
