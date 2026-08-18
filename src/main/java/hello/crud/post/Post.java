package hello.crud.post;

import java.util.ArrayList;
import java.util.List;

import hello.crud.comment.Comment;
import hello.crud.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String content;
	private long viewCount = 0L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToMany(mappedBy = "post")
	private List<Comment> comments = new ArrayList<>();

	@Builder
	public Post(String title, String content, Member member) {
		this.title = title;
		this.content = content;
		this.member = member;
	}

}
