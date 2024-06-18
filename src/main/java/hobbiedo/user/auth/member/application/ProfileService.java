package hobbiedo.user.auth.member.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hobbiedo.user.auth.global.api.code.status.ErrorStatus;
import hobbiedo.user.auth.global.exception.MemberExceptionHandler;
import hobbiedo.user.auth.member.domain.Member;
import hobbiedo.user.auth.member.dto.request.ProfileImageRequestDto;
import hobbiedo.user.auth.member.dto.response.ProfileResponseDto;
import hobbiedo.user.auth.member.infrastructure.MemberProfileRepository;
import hobbiedo.user.auth.member.infrastructure.MemberRepository;
import hobbiedo.user.auth.member.vo.request.ProfileImageRequestVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

	private final MemberProfileRepository memberProfileRepository;

	public ProfileResponseDto getProfile(String uuid) {

		Member member = memberProfileRepository.findByUuid(uuid)
			.orElseThrow(() -> new MemberExceptionHandler(ErrorStatus.NOT_FOUND_MEMBER));

		return ProfileResponseDto.builder()
			.uuid(member.getUuid())
			.name(member.getName())
			.email(member.getEmail())
			.phoneNumber(member.getPhoneNumber())
			.birth(member.getBirth())
			.gender(member.getGender())
			.profileImageUrl(member.getImageUrl())
			.profileMessage(member.getProfileMessage())
			.build();
	}

	@Transactional
	public void updateProfileImage(String uuid, ProfileImageRequestDto profileImageRequestDto) {

		Member member = memberProfileRepository.findByUuid(uuid)
			.orElseThrow(() -> new MemberExceptionHandler(ErrorStatus.NOT_FOUND_MEMBER));

		Member updateMember = Member.builder()
			.id(member.getId())
			.name(member.getName())
			.uuid(member.getUuid())
			.email(member.getEmail())
			.phoneNumber(member.getPhoneNumber())
			.active(member.getActive())
			.gender(member.getGender())
			.birth(member.getBirth())
			.profileMessage(member.getProfileMessage())
			.imageUrl(profileImageRequestDto.getProfileImageUrl())
			.build();

		memberProfileRepository.save(updateMember);
	}

}