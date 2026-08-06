package hello.crud.post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

@Repository
public class PostRepositoryImpl implements PostRepository {

	private final Map<Long, Post> store = new ConcurrentHashMap<>();
	private final AtomicLong sequence = new AtomicLong(0);

	@Override
	public Post save(Post post) {
		post.assignId(sequence.incrementAndGet());
		store.put(post.getId(), post);
		return post;
	}

	@Override
	public Optional<Post> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

	@Override
	public List<Post> findAll() {
		return new ArrayList<>(store.values());
	}

	@Override
	public void clearStore() {
		store.clear();
	}
}
