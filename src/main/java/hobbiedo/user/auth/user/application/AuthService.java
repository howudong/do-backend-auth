package hobbiedo.user.auth.user.application;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobbiedo.user.auth.global.api.code.status.ErrorStatus;
import hobbiedo.user.auth.global.config.jwt.JwtUtil;
import hobbiedo.user.auth.global.config.jwt.TokenType;
import hobbiedo.user.auth.global.exception.MemberExceptionHandler;
import hobbiedo.user.auth.user.domain.RefreshToken;
import hobbiedo.user.auth.user.dto.request.LoginRequestDTO;
import hobbiedo.user.auth.user.dto.request.ReIssueRequestDTO;
import hobbiedo.user.auth.user.dto.response.LoginResponseDTO;
import hobbiedo.user.auth.user.infrastructure.MemberRepository;
import hobbiedo.user.auth.user.infrastructure.RefreshTokenRepository;
import hobbiedo.user.auth.user.vo.response.LoginResponseVO;
import hobbiedo.user.auth.user.vo.response.ReIssueResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
	private final JwtUtil jwtUtil;
	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public LoginResponseVO login(LoginRequestDTO loginDTO) {
		LoginResponseDTO user = getUuidByLoginId(loginDTO.getLoginId());
		validatePassword(loginDTO.getPassword(), user.getPassword());

		String accessToken = jwtUtil.createJwt(user.getUuid(), TokenType.ACCESS_TOKEN);
		String refreshToken = jwtUtil.createJwt(user.getUuid(), TokenType.REFRESH_TOKEN);

		saveToRedis(refreshToken, TokenType.REFRESH_TOKEN, user.getUuid());
		return LoginResponseVO
				.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	private void saveToRedis(String refreshToken, TokenType tokenType, String uuid) {
		refreshTokenRepository.save(RefreshToken
				.builder()
				.id(uuid)
				.refresh(refreshToken)
				.expiration(System.currentTimeMillis() + tokenType.getExpireTime())
				.build());
	}

	@Transactional
	public ReIssueResponseVO reIssueToken(ReIssueRequestDTO reIssueDTO) {
		validateRefreshToken(reIssueDTO.getRefreshToken());

		String receivedRefreshToken = reIssueDTO.getRefreshToken();
		String uuid = jwtUtil.getUuid(receivedRefreshToken);
		String newAccessToken = jwtUtil.createJwt(uuid, TokenType.ACCESS_TOKEN);
		String newRefreshToken = jwtUtil.createJwt(uuid, TokenType.REFRESH_TOKEN);

		/* 기존 Refresh Token 삭제 후, 새로 생성된 값을 저장*/
		refreshTokenRepository.deleteByRefresh(receivedRefreshToken);
		saveToRedis(newRefreshToken, TokenType.REFRESH_TOKEN, uuid);

		return ReIssueResponseVO
				.builder()
				.accessToken(newAccessToken)
				.refreshToken(newRefreshToken)
				.build();
	}

	private void validateRefreshToken(String receivedRefreshToken) {
		if (!refreshTokenRepository.existsByRefresh(receivedRefreshToken)) {
			throw new MemberExceptionHandler(ErrorStatus.NOT_EXIST_TOKEN);
		}
		if (jwtUtil.isExpired(receivedRefreshToken)) {
			throw new MemberExceptionHandler(ErrorStatus.USER_REFRESH_EXPIRED);
		}
		if (jwtUtil.getTokenType(receivedRefreshToken) != TokenType.REFRESH_TOKEN) {
			throw new MemberExceptionHandler(ErrorStatus.NOT_REFRESH_TOKEN_TYPE);
		}

	}

	private LoginResponseDTO getUuidByLoginId(String loginId) {
		return memberRepository
				.findByLoginId(loginId)
				.orElseThrow(() -> new MemberExceptionHandler(ErrorStatus.USER_INTEGRATED_LOGIN_FAIL));
	}

	private void validatePassword(String input, String origin) {
		if (!passwordEncoder.matches(input, origin)) {
			throw new MemberExceptionHandler(ErrorStatus.USER_INTEGRATED_LOGIN_FAIL);
		}
	}
}
