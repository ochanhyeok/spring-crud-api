package hello.crud.comment;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {

	void save(Comment comment);

	Optional<Comment> findById(Long id);

	List<Comment> findAll();

	void deleteAll();
}
