package hello.crud.support;

import java.net.CookieManager;
import java.net.http.HttpClient;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import hello.crud.auth.dto.LoginRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApiTestSupport {

	@LocalServerPort
	protected int port;
	protected RestClient restClient;
	protected ApiTestDataFactory apiTestDataFactory;

	@Autowired
	protected DatabaseCleaner databaseCleaner;

	@BeforeEach
	protected void setUpRestClient() {
		HttpClient httpClient = HttpClient.newBuilder()
			.cookieHandler(new CookieManager())
			.build();

		restClient = RestClient.builder()
			.baseUrl("http://localhost:" + port)
			.requestFactory(new JdkClientHttpRequestFactory(httpClient))
			.build();

		apiTestDataFactory = new ApiTestDataFactory(restClient);
	}

	@AfterEach
	protected void cleanUp() {
		databaseCleaner.clean();
	}

	protected void login(String loginId) {
		LoginRequest request = new LoginRequest();
		request.setLoginId(loginId);
		request.setPassword("1234");

		restClient.post().uri("/api/auth/login")
			.body(request)
			.retrieve()
			.toBodilessEntity();
	}
}
