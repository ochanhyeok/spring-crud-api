package hello.crud.post;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {

	void save(Post post);

	Optional<Post> findById(Long id);

	List<Post> findAll();

	void deleteAll();

}
