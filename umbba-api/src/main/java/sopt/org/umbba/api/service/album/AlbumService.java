package sopt.org.umbba.api.service.album;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sopt.org.umbba.api.controller.album.dto.request.CreateAlbumRequestDto;
import sopt.org.umbba.api.controller.album.dto.response.AlbumResponseDto;
import sopt.org.umbba.common.exception.ErrorType;
import sopt.org.umbba.common.exception.model.CustomException;
import sopt.org.umbba.domain.domain.album.Album;
import sopt.org.umbba.domain.domain.album.repository.AlbumRepository;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.user.User;
import sopt.org.umbba.domain.domain.user.repository.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlbumService {

	private final AlbumRepository albumRepository;
	private final UserRepository userRepository;

	@Transactional
	public Long createAlbum(final CreateAlbumRequestDto request, final String imgUrl, final Long userId) {

		User user = getUserById(userId);
		Parentchild parentchild = user.getParentChild();
		Album album = Album.builder()
			.title(request.getTitle())
			.content(request.getContent())
			.imgUrl(imgUrl)
			.writer(user.getUsername())
			.parentchild(parentchild)
			.build();
		albumRepository.save(album);
		album.setParentchild(parentchild);
		parentchild.addAlbum(album);

		return album.getId();
	}

	@Transactional
	public String deleteAlbum(final Long albumId, final Long userId) {

		User user = getUserById(userId);
		Album album = getAlbumById(albumId);

		album.deleteParentchild();
		user.getParentChild().deleteAlbum(album);
		albumRepository.delete(album);

		return album.getImgUrl();
	}

	public List<AlbumResponseDto> getAlbumList(final Long userId) {
		User user = getUserById(userId);
		List<Album> albumList = albumRepository.findAllByParentchildOrderByCreatedAtDesc(
			user.getParentChild());

		return albumList.stream()
			.map(AlbumResponseDto::of)
			.collect(Collectors.toList());
	}

	private User getUserById(Long userId) {  // TODO userId -> Parentchild 한번에 가져오기
		return userRepository.findById(userId).orElseThrow(
			() -> new CustomException(ErrorType.INVALID_USER)
		);
	}

	private Album getAlbumById(Long albumId) {
		return albumRepository.findById(albumId).orElseThrow(
			() -> new CustomException(ErrorType.NOT_FOUND_ALBUM)
		);
	}
}
