package hello.crud.member;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.member.dto.MemberResponse;

@SpringBootTest
class MemberServiceTest {

	@Autowired
	MemberService memberService;
	@Autowired
	MemberRepository memberRepository;

	@AfterEach
	void afterEach() {
		memberRepository.clearStore();
	}

	@Test
	void save() {
		// given
		MemberCreateRequest request = createRequest("ochhs0829");

		// when
		MemberResponse response = memberService.create(request);

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response.getName()).isEqualTo("chanhyeok");
		assertThat(response.getLoginId()).isEqualTo("ochhs0829");
	}

	@Test
	void findOne() {
		// given
		MemberCreateRequest request = createRequest("ochhs0829");
		MemberResponse response = memberService.create(request);

		// when
		MemberResponse saved = memberService.findOne(response.getId());

		// then
		assertThat(memberService.findOne(saved.getId())).isNotNull();
		assertThat(saved.getLoginId()).isEqualTo("ochhs0829");
	}

	@Test
	void findAll() {
		// given
		MemberCreateRequest request1 = createRequest("ochhs0829");
		MemberCreateRequest request2 = createRequest("ochhs0822");
		MemberResponse response1 = memberService.create(request1);
		MemberResponse response2 = memberService.create(request2);

		// when
		List<MemberResponse> responses = memberService.findAll();

		// then
		assertThat(responses.size()).isEqualTo(2);
		assertThat(responses.get(0).getId()).isEqualTo(response1.getId());
		assertThat(responses.get(1).getId()).isEqualTo(response2.getId());
		assertThat(responses.get(0).getLoginId()).isEqualTo("ochhs0829");
		assertThat(responses.get(1).getLoginId()).isEqualTo("ochhs0822");
	}

	@Test
	void findOne_없는_id_예외() {
		// given
		MemberCreateRequest request = createRequest("ochhs0829");
	 	memberService.create(request);

		// when & then
		assertThatThrownBy(() -> memberService.findOne(999L))
			.isInstanceOf(NoSuchElementException.class);
	}

	private MemberCreateRequest createRequest(String loginId) {
		MemberCreateRequest request = new MemberCreateRequest();
		request.setLoginId(loginId);
		request.setName("chanhyeok");
		request.setPassword("oh123");
		return request;
	}
}