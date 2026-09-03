package hello.crud.member;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hello.crud.common.DuplicateException;
import hello.crud.common.NotFoundException;
import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.member.dto.MemberResponse;
import hello.crud.support.ServiceTestSupport;

class MemberServiceTest extends ServiceTestSupport {

	@Autowired
	private MemberService memberService;

	@Test
	void save() {
		// given
		MemberCreateRequest request = createRequest("ohchanhyeok123");

		// when
		MemberResponse response = memberService.create(request);

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response.getName()).isEqualTo("chanhyeok");
		assertThat(response.getLoginId()).isEqualTo("ohchanhyeok123");
	}

	@Test
	void findOne() {
		// given
		MemberResponse response = memberService.create(createRequest("ohchanhyeok123"));

		// when
		MemberResponse saved = memberService.findOne(response.getId());

		// then
		assertThat(memberService.findOne(saved.getId())).isNotNull();
		assertThat(saved.getLoginId()).isEqualTo("ohchanhyeok123");
	}

	@Test
	void findAll() {
		// given
		MemberResponse response1 = memberService.create(createRequest("ohchanhyeok123"));
		MemberResponse response2 = memberService.create(createRequest("ochhs0822"));

		// when
		List<MemberResponse> responses = memberService.findAll();

		// then
		assertThat(responses.size()).isEqualTo(2);
		assertThat(responses.get(0).getId()).isEqualTo(response1.getId());
		assertThat(responses.get(1).getId()).isEqualTo(response2.getId());
		assertThat(responses.get(0).getLoginId()).isEqualTo("ohchanhyeok123");
		assertThat(responses.get(1).getLoginId()).isEqualTo("ochhs0822");
	}

	@Test
	void findOne_없는_id_예외() {
		// given
		memberService.create(createRequest("ohchanhyeok123"));

		// when & then
		assertThatThrownBy(() -> memberService.findOne(999L))
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	void 중복_아이디로_가입하면_예외() {
		// given
		memberService.create(createRequest("test1"));

		// when & then
		assertThatThrownBy(() -> memberService.create(createRequest("test1")))
			.isInstanceOf(DuplicateException.class);
	}

	private MemberCreateRequest createRequest(String loginId) {
		MemberCreateRequest request = new MemberCreateRequest();
		request.setLoginId(loginId);
		request.setName("chanhyeok");
		request.setPassword("oh123");
		return request;
	}
}