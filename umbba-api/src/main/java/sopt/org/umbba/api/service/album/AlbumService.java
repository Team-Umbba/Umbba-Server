package sopt.org.umbba.api.service.album;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sopt.org.umbba.api.controller.album.dto.request.CreateAlbumRequestDto;
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

	public Long createAlbum(final CreateAlbumRequestDto request, Long userId) {

		User user = getByUser(userId);
		Parentchild parentchild = user.getParentChild();
		Album album = Album.builder()
			.title(request.getTitle())
			.content(request.getContent())
			.imgUrl(request.getImgFileName())
			.username(user.getUsername())
			.parentchild(parentchild)
			.build();
		albumRepository.save(album);
		album.setParentchild(parentchild);
		parentchild.addAlbum(album);

		return album.getId();
	}

	private User getByUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(
			() -> new CustomException(ErrorType.INVALID_USER)
		);
	}

}
