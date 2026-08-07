package hello.crud.member;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import hello.crud.member.dto.MemberCreateRequest;
import hello.crud.member.dto.MemberResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberApiTest {

	@LocalServerPort
	int port;

	@Autowired
	MemberRepository memberRepository;

	RestClient restClient;

	@BeforeEach
	void beforeEach() {
		restClient = RestClient.create("http://localhost:" + port);
	}

	@AfterEach
	void afterEach() {
		memberRepository.clearStore();
	}

	@Test
	void 회원_생성() {
		// given & when
		MemberResponse response = createMember("ochhs0829", "chanhyeok");

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response.getLoginId()).isEqualTo("ochhs0829");
		assertThat(response.getName()).isEqualTo("chanhyeok");
	}

	@Test
	void 회원_찾기() {
		// given
		Long memberId = createMember("ochhs0829", "오찬혁").getId();

		// when
		MemberResponse response = restClient.get()
			.uri("/api/members/" + memberId)
			.retrieve()
			.body(MemberResponse.class);

		// then
		assertThat(response.getId()).isNotNull();
		assertThat(response.getId()).isEqualTo(memberId);
		assertThat(response.getLoginId()).isEqualTo("ochhs0829");
		assertThat(response.getName()).isEqualTo("오찬혁");
	}

	@Test
	void 회원_목록() {
		// given
		createMember("ochhs0829", "오찬혁");
		createMember("ochhs0231", "찬혁오");

		// when
		List<MemberResponse> responses = restClient.get()
			.uri("/api/members")
			.retrieve()
			.body(new ParameterizedTypeReference<List<MemberResponse>>() {
			});

		// then
		assertThat(responses.size()).isEqualTo(2);
		assertThat(responses).extracting(MemberResponse::getLoginId)
			.containsExactlyInAnyOrder("ochhs0829", "ochhs0231");
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

	private MemberResponse createMember(String loginId, String name) {
		MemberCreateRequest request = new MemberCreateRequest();
		request.setLoginId(loginId);
		request.setName(name);
		request.setPassword("12345");
		return restClient.post().uri("/api/members")
			.body(request).retrieve().body(MemberResponse.class);
	}
}