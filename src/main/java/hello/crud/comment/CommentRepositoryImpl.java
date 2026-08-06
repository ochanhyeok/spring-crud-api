package hello.crud.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

	private final Map<Long, Comment> store = new ConcurrentHashMap<>();
	private final AtomicLong sequence = new AtomicLong(0);

	@Override
	public Comment save(Comment comment) {
		comment.assignId(sequence.incrementAndGet());
		store.put(comment.getId(), comment);
		return comment;
	}

	@Override
	public Optional<Comment> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

	@Override
	public List<Comment> findAll() {
		return new ArrayList<>(store.values());
	}

	@Override
	public void clearStore() {
		store.clear();
	}
}
