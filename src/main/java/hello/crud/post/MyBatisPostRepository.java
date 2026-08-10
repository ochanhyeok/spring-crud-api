package hello.crud.post;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisPostRepository implements PostRepository {

	private final PostMapper postMapper;

	@Override
	public Post save(Post post) {
		postMapper.save(post);
		return post;
	}

	@Override
	public Optional<Post> findById(Long id) {
		return postMapper.findById(id);
	}

	@Override
	public List<Post> findAll() {
		return postMapper.findAll();
	}

	@Override
	public void clearStore() {
		postMapper.deleteAll();
	}
}
