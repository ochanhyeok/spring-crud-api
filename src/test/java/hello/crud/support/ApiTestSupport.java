package hello.crud.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

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
		restClient = RestClient.create("http://localhost:" + port);
		apiTestDataFactory = new ApiTestDataFactory(restClient);
	}

	@AfterEach
	protected void cleanUp() {
		databaseCleaner.clean();
	}
}
