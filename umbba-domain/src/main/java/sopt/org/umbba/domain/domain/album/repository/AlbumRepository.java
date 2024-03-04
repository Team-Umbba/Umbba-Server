package sopt.org.umbba.domain.domain.album.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.umbba.domain.domain.album.Album;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;

public interface AlbumRepository extends JpaRepository<Album, Long> {

	List<Album> findAllByParentchildOrderByCreatedAtDesc(Parentchild parentchild);

}
