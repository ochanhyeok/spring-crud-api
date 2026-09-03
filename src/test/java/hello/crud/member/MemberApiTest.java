package hello.crud.member;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;

import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.member.dto.MemberResponse;
import hello.crud.support.ApiTestSupport;

class MemberApiTest extends ApiTestSupport {

	@Test
	void 회원_생성() {
		// given & when
		MemberResponse response = createMember("ohchanhyeok123", "chanhyeok");

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response.getLoginId()).isEqualTo("ohchanhyeok123");
		assertThat(response.getName()).isEqualTo("chanhyeok");
	}

	@Test
	void 회원_찾기() {
		// given
		Long memberId = apiTestDataFactory.createMember("ohchanhyeok123", "오찬혁").getId();

		// when
		MemberResponse response = restClient.get()
			.uri("/api/members/" + memberId)
			.retrieve()
			.body(MemberResponse.class);

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response.getId()).isEqualTo(memberId);
		assertThat(response.getLoginId()).isEqualTo("ohchanhyeok123");
		assertThat(response.getName()).isEqualTo("오찬혁");
	}

	@Test
	void 회원_목록() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "오찬혁");
		apiTestDataFactory.createMember("ochhs0231", "찬혁오");

		// when
		List<MemberResponse> responses = restClient.get()
			.uri("/api/members")
			.retrieve()
			.body(new ParameterizedTypeReference<List<MemberResponse>>() {
			});

		// then
		assertThat(responses.size()).isEqualTo(2);
		assertThat(responses).extracting(MemberResponse::getLoginId)
			.containsExactlyInAnyOrder("ohchanhyeok123", "ochhs0231");
		assertThat(responses).extracting(MemberResponse::getName)
			.containsExactlyInAnyOrder("오찬혁", "찬혁오");
	}

	@Test
	void 없는_회원_조회() {
		assertThatThrownBy(() ->
			restClient.get().uri("/api/members/999")
				.retrieve().body(MemberResponse.class)
		).isInstanceOf(HttpClientErrorException.NotFound.class);
	}

	@Test
	void 잘못된_입력_400() {
		// given
		MemberCreateRequest request = new MemberCreateRequest();
		request.setLoginId("");
		request.setPassword("12");

		// when & then
		assertThatThrownBy(() -> restClient.post().uri("/api/members")
			.body(request).retrieve().body(MemberResponse.class))
			.isInstanceOf(HttpClientErrorException.BadRequest.class);
	}

	@Test
	void 중복_아이디_가입_409() {
		// given
		apiTestDataFactory.createMember("ohchanhyeok123", "손흥민");

		// when & then
		assertThatThrownBy(() -> apiTestDataFactory.createMember("ohchanhyeok123", "박지성"))
			.isInstanceOf(HttpClientErrorException.Conflict.class)
			.satisfies(e -> assertThat(
				((HttpClientErrorException)e).getResponseBodyAsString()
			).contains("DUPLICATE_LOGIN_ID"));
	}

	public MemberResponse createMember(String loginId, String name) {
		MemberCreateRequest request = new MemberCreateRequest();
		request.setLoginId(loginId);
		request.setName(name);
		request.setPassword("1234");
		return restClient.post().uri("/api/members")
			.body(request)
			.retrieve()
			.body(MemberResponse.class);
	}
}