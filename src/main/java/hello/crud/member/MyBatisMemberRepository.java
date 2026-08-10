package hello.crud.member;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MyBatisMemberRepository implements MemberRepository {

	private final MemberMapper memberMapper;

	@Override
	public Member save(Member member) {
		memberMapper.save(member);
		return member;
	}

	@Override
	public Optional<Member> findById(Long id) {
		return memberMapper.findById(id);
	}

	@Override
	public List<Member> findAll() {
		return memberMapper.findAll();
	}

	@Override
	public void clearStore() {
		memberMapper.deleteAll();
	}
}
