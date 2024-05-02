package sopt.org.umbba.api.controller.album;

import static sopt.org.umbba.api.config.jwt.JwtProvider.*;
import static sopt.org.umbba.api.service.album.AlbumService.*;
import static sopt.org.umbba.common.exception.SuccessType.*;
import static sopt.org.umbba.external.s3.S3BucketPrefix.*;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.umbba.api.controller.album.dto.request.AlbumImgUrlRequestDto;
import sopt.org.umbba.api.controller.album.dto.request.CreateAlbumRequestDto;
import sopt.org.umbba.api.controller.album.dto.response.AlbumResponseDto;
import sopt.org.umbba.api.service.album.AlbumService;
import sopt.org.umbba.common.exception.dto.ApiResponse;
import sopt.org.umbba.external.s3.PreSignedUrlDto;
import sopt.org.umbba.external.s3.S3BucketPrefix;
import sopt.org.umbba.external.s3.S3Service;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {

	private final AlbumService albumService;
	private final S3Service s3Service;

	private static final String DEFAULT_ALBUM_IMG = "default_img.png";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse createAlbum(@Valid @RequestBody final CreateAlbumRequestDto request, final Principal principal, HttpServletResponse response) {
		String imgUrl = s3Service.getS3ImgUrl(ALBUM_PREFIX.getValue(), request.getImgFileName());
		Long albumId = albumService.createAlbum(request, imgUrl, getUserFromPrincial(principal));
		response.setHeader("Location", "/album/" + albumId);
		return ApiResponse.success(CREATE_ALBUM_SUCCESS);
	}

	// PreSigned Url 이용 (클라이언트에서 해당 URL로 업로드)
	@PatchMapping("/image")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<PreSignedUrlDto> getImgPreSignedUrl(@RequestBody final AlbumImgUrlRequestDto request) {
		return ApiResponse.success(GET_PRE_SIGNED_URL_SUCCESS, s3Service.getPreSignedUrl(S3BucketPrefix.of(request.getImgPrefix())));
	}

	@DeleteMapping("/{albumId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse deleteAlbum(@PathVariable final Long albumId, final Principal principal) {
		String imgUrl = albumService.deleteAlbum(albumId, getUserFromPrincial(principal));
		if (!imgUrl.equals(ALBUM_EXAMPLE)) {   // Example Album의 이미지는 삭제하지 X
			s3Service.deleteS3Image(imgUrl);
		}
		return ApiResponse.success(DELETE_ALBUM_SUCCESS);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<List<AlbumResponseDto>> getAlbumList(final Principal principal) {
		String defaultImgUrl = s3Service.getS3ImgUrl(ALBUM_PREFIX.getValue(), DEFAULT_ALBUM_IMG);
		return ApiResponse.success(GET_ALBUM_LIST_SUCCESS, albumService.getAlbumList(defaultImgUrl, getUserFromPrincial(principal)));
	}
}