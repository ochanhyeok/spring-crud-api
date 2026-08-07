package hello.crud.member;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.member.dto.MemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PostMapping
	public MemberResponse createMember(@RequestBody @Valid MemberCreateRequest request) {
		return memberService.create(request);
	}

	@GetMapping
	public List<MemberResponse> getMembers() {
		return memberService.findAll();
	}

	@GetMapping("/{memberId}")
	public MemberResponse getMember(@PathVariable Long memberId) {
		return memberService.findOne(memberId);
	}
}
