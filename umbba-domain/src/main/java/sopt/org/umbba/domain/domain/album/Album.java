package sopt.org.umbba.domain.domain.album;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.umbba.domain.domain.common.AuditingTimeEntity;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Album extends AuditingTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "album_id")
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	@Column(columnDefinition = "TEXT")
	private String imgUrl;

	@Column(nullable = false)
	private String writer;

	@ManyToOne
	@JoinColumn(name = "parentchild_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Parentchild parentchild;

	@Builder
	private Album(String title, String content, String imgUrl, String writer, Parentchild parentchild) {
		this.title = title;
		this.content = content;
		this.imgUrl = imgUrl;
		this.writer = writer;
		this.parentchild = parentchild;
	}

	public void setParentchild(Parentchild parentchild) {
		this.parentchild = parentchild;

		if (!parentchild.getAlbumList().contains(this)) {
			parentchild.getAlbumList().add(this);
		}
	}

	public void deleteParentchild() {
		this.parentchild = null;
	}
}
