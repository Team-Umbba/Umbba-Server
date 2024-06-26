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

	public static final String ALBUM_EXAMPLE = "example";

	@Transactional
	public Long createAlbum(final CreateAlbumRequestDto request, final String imgUrl, final Long userId) {

		User user = getUserById(userId);
		Parentchild parentchild = getParentchildByUser(user);

		if (parentchild.isOverMaxAlbumLimit()) {
			throw new CustomException(ErrorType.MAX_LIMIT_ALBUM_UPLOAD);
		}

		// 앨범을 처음 등록하는 경우
		if (parentchild.getAlbumList().isEmpty() && !parentchild.isFirstAlbumUpload()) {
			parentchild.updateFirstAlbumUpload();
		}

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
		Parentchild parentchild = getParentchildByUser(user);

		// Sample Album을 삭제할 경우
		if (albumId.equals(0L)) {
			parentchild.updateDeleteSampleAlbum();
			return ALBUM_EXAMPLE;
		}

		Album album = getAlbumById(albumId);

		album.deleteParentchild();
		parentchild.deleteAlbum(album);
		albumRepository.delete(album);

		return album.getImgUrl();
	}

	public List<AlbumResponseDto> getAlbumList(final String defaultImgUrl, final Long userId) {
		User user = getUserById(userId);
		Parentchild parentchild = getParentchildByUser(user);
		List<Album> albumList = albumRepository.findAllByParentchildOrderByCreatedAtDesc(
			parentchild);

		// Album을 아직 한번도 등록하지 않은 경우
		if (albumList.isEmpty() && !parentchild.isFirstAlbumUpload() && !parentchild.isDeleteSampleAlbum()) {
			return List.of(AlbumResponseDto.of(createAlbumExample(defaultImgUrl)));
		}

		return albumList.stream()
			.map(AlbumResponseDto::of)
			.collect(Collectors.toList());
	}

	private Album createAlbumExample(final String defaultImgUrl) {
		return new Album(0L, "사진의 제목을 입력할 수 있어요", "사진에 대해 소개해요", defaultImgUrl, "직성자");
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

	private Parentchild getParentchildByUser(User user) {
		Parentchild parentchild = user.getParentChild();
		if (parentchild == null) {
			throw new CustomException(ErrorType.USER_HAVE_NO_PARENTCHILD);
		}

		return parentchild;
	}
}
