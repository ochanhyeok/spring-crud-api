package hello.crud.member;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

	void save(Member member);

	Optional<Member> findById(Long id);

	List<Member> findAll();

	void deleteAll();
}
