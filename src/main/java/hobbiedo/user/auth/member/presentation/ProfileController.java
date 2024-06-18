package hobbiedo.user.auth.member.presentation;

import static hobbiedo.user.auth.global.api.code.status.SuccessStatus.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hobbiedo.user.auth.global.api.ApiResponse;
import hobbiedo.user.auth.member.application.ProfileService;
import hobbiedo.user.auth.member.dto.response.ProfileResponseDto;
import hobbiedo.user.auth.member.vo.response.ProfileResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@Tag(name = "Profile", description = "사용자 프로필 서비스")
public class ProfileController {

	private final ProfileService profileService;

	// 프로필 상세 조회
	@GetMapping("/member/profile")
	@Operation(summary = "프로필 상세 조회", description = "사용자의 프로필 상세 정보를 조회합니다.")
    public ApiResponse<ProfileResponseVo> getProfile(
		@RequestHeader(name = "Uuid") String uuid) {

		ProfileResponseDto profileResponseDto = profileService.getProfile(uuid);

		return ApiResponse.onSuccess(
			GET_PROFILE_DETAIL_SUCCESS,
			ProfileResponseVo.profileDtoToProfileVo(profileResponseDto)
		);
	}

}
