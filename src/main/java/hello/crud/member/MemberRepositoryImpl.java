package hello.crud.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

// @Repository
public class MemberRepositoryImpl implements MemberRepository {

	private final Map<Long, Member> store = new ConcurrentHashMap<>();
	private final AtomicLong sequence = new AtomicLong(0);

	@Override
	public Member save(Member member) {
		member.assignId(sequence.incrementAndGet());
		store.put(member.getId(), member);
		return member;
	}

	@Override
	public Optional<Member> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

	@Override
	public List<Member> findAll() {
		return new ArrayList<>(store.values());
	}

	@Override
	public void clearStore() {
		store.clear();
	}
}
