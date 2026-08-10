package hello.crud.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisCommentRepository implements CommentRepository {

	private final CommentMapper commentMapper;

	@Override
	public Comment save(Comment comment) {
		commentMapper.save(comment);
		return comment;
	}

	@Override
	public Optional<Comment> findById(Long id) {
		return commentMapper.findById(id);
	}

	@Override
	public List<Comment> findAll() {
		return commentMapper.findAll();
	}

	@Override
	public void clearStore() {
		commentMapper.deleteAll();
	}
}
