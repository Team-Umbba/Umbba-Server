package sopt.org.umbba.api.controller.album;

import static sopt.org.umbba.api.config.jwt.JwtProvider.*;
import static sopt.org.umbba.common.exception.SuccessType.*;

import java.security.Principal;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sopt.org.umbba.api.controller.album.dto.request.CreateAlbumRequestDto;
import sopt.org.umbba.api.service.album.AlbumService;
import sopt.org.umbba.common.exception.dto.ApiResponse;

@RestController
@RequestMapping("/album")
@RequiredArgsConstructor
public class AlbumController {

	private final AlbumService albumService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse createAlbum(@Valid @RequestBody final CreateAlbumRequestDto request, final Principal principal, HttpServletResponse response) {
		Long albumId = albumService.createAlbum(request, getUserFromPrincial(principal));
		response.setHeader("Location", "/album/" + albumId);
		return ApiResponse.success(CREATE_ALBUM_SUCCESS);
	}
}
