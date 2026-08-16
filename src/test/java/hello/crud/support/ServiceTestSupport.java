package hello.crud.support;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class ServiceTestSupport {

	@Autowired
	protected TestDataFactory testDataFactory;
	@Autowired
	protected DatabaseCleaner databaseCleaner;

	@AfterEach
	protected void cleanUp() {
		databaseCleaner.clean();
	}
}
