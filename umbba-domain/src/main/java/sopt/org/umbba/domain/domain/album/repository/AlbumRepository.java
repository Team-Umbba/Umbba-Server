package sopt.org.umbba.domain.domain.album.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sopt.org.umbba.domain.domain.album.Album;

public interface AlbumRepository extends JpaRepository<Album, Long> {

}
