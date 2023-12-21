package org.ftclub.cabinet.user.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ftclub.cabinet.alarm.domain.AlarmType;
import org.ftclub.cabinet.alarm.dto.AlarmTypeResponseDto;
import org.ftclub.cabinet.alarm.service.AlarmCommandService;
import org.ftclub.cabinet.alarm.service.AlarmQueryService;
import org.ftclub.cabinet.cabinet.domain.Cabinet;
import org.ftclub.cabinet.cabinet.domain.LentType;
import org.ftclub.cabinet.cabinet.repository.CabinetOptionalFetcher;
import org.ftclub.cabinet.dto.BlockedUserPaginationDto;
import org.ftclub.cabinet.dto.CabinetDto;
import org.ftclub.cabinet.dto.ClubUserListDto;
import org.ftclub.cabinet.dto.LentExtensionPaginationDto;
import org.ftclub.cabinet.dto.LentExtensionResponseDto;
import org.ftclub.cabinet.dto.MyProfileResponseDto;
import org.ftclub.cabinet.dto.OverdueUserCabinetDto;
import org.ftclub.cabinet.dto.OverdueUserCabinetPaginationDto;
import org.ftclub.cabinet.dto.UpdateAlarmRequestDto;
import org.ftclub.cabinet.dto.UserBlockedInfoDto;
import org.ftclub.cabinet.dto.UserCabinetDto;
import org.ftclub.cabinet.dto.UserCabinetPaginationDto;
import org.ftclub.cabinet.dto.UserProfileDto;
import org.ftclub.cabinet.dto.UserProfilePaginationDto;
import org.ftclub.cabinet.dto.UserSessionDto;
import org.ftclub.cabinet.lent.repository.LentOptionalFetcher;
import org.ftclub.cabinet.mapper.CabinetMapper;
import org.ftclub.cabinet.mapper.UserMapper;
import org.ftclub.cabinet.user.domain.AdminRole;
import org.ftclub.cabinet.user.domain.AlarmOptIn;
import org.ftclub.cabinet.user.domain.AlarmStatus;
import org.ftclub.cabinet.user.domain.BanHistory;
import org.ftclub.cabinet.user.domain.LentExtension;
import org.ftclub.cabinet.user.domain.User;
import org.ftclub.cabinet.user.domain.UserRole;
import org.ftclub.cabinet.user.repository.LentExtensionOptionalFetcher;
import org.ftclub.cabinet.user.repository.UserOptionalFetcher;
import org.ftclub.cabinet.utils.DateUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserFacadeServiceImpl implements UserFacadeService {

	private final UserService userService;
	private final UserOptionalFetcher userOptionalFetcher;
	private final LentOptionalFetcher lentOptionalFetcher;
	private final UserMapper userMapper;
	private final CabinetOptionalFetcher cabinetOptionalFetcher;
	private final CabinetMapper cabinetMapper;
	private final LentExtensionService lentExtensionService;
	private final LentExtensionOptionalFetcher lentExtensionOptionalFetcher;
	private final AlarmCommandService alarmCommandService;
	private final AlarmQueryService alarmQueryService;

	@Override
	public MyProfileResponseDto getMyProfile(UserSessionDto user) {
		log.debug("Called getMyProfile: {}", user.getName());

		Cabinet cabinet = lentOptionalFetcher.findActiveLentCabinetByUserId(user.getUserId());
		BanHistory banHistory = userOptionalFetcher.findRecentActiveBanHistory(user.getUserId(),
				LocalDateTime.now());

		LentExtensionResponseDto activeLentExtension = lentExtensionService.getActiveLentExtension(
				user);

//		List<AlarmOptIn> alarmOptIns = alarmQueryService.findAllAlarmOptInByUserId(
//				user.getUserId());
//		List<AlarmType> alarmTypes = alarmOptIns.stream().map(AlarmOptIn::getAlarmType)
//				.collect(Collectors.toList());
//		AlarmTypeResponseDto alarmTypeResponseDto = AlarmTypeResponseDto.builder()
//				.alarmTypes(alarmTypes).build();

		AlarmStatus userAlarmStatus = alarmQueryService.findAlarmStatusByUserId(
				user.getUserId());
		AlarmTypeResponseDto.builder().alarmStatus(userAlarmStatus).build();

		return userMapper.toMyProfileResponseDto(user, cabinet, banHistory,
				activeLentExtension,
				AlarmTypeResponseDto.builder().alarmStatus(userAlarmStatus).build());
	}

	@Override
	public BlockedUserPaginationDto getAllBanUsers(Integer page, Integer size, LocalDateTime now) {
		log.debug("Called getAllBanUsers");
		if (size <= 0) {
			size = Integer.MAX_VALUE;
		}
		PageRequest pageable = PageRequest.of(page, size);
		Page<BanHistory> activeBanList = userOptionalFetcher.findPaginationActiveBanHistories(
				pageable, now);
		List<UserBlockedInfoDto> userBlockedInfoDtos = activeBanList.stream().map(
						banHistory -> userMapper.toUserBlockedInfoDto(
								banHistory, banHistory.getUser()))
				.collect(Collectors.toList());
		return userMapper.toBlockedUserPaginationDto(userBlockedInfoDtos,
				activeBanList.getTotalElements());
	}

	@Override
	public UserProfilePaginationDto getUserProfileListByPartialName(String name, Integer page,
			Integer size) {
		log.debug("Called getUserProfileListByPartialName: {}", name);
		// todo - size가 0일 때 모든 데이터를 가져오기
		if (size <= 0) {
			size = Integer.MAX_VALUE;
		}
		PageRequest pageable = PageRequest.of(page, size);
		Page<User> users = userOptionalFetcher.findUsersByPartialName(name, pageable);
		List<UserProfileDto> userProfileDtoList = users.stream()
				.map(userMapper::toUserProfileDto).collect(Collectors.toList());
		return userMapper.toUserProfilePaginationDto(userProfileDtoList,
				users.getTotalElements());
	}

	@Override
	public UserCabinetPaginationDto findUserCabinetListByPartialName(String name, Integer page,
			Integer size) {
		log.debug("Called findUserCabinetListByPartialName: {}", name);
		// todo - size가 0일 때 모든 데이터를 가져오기
		if (size <= 0) {
			size = Integer.MAX_VALUE;
		}
		PageRequest pageable = PageRequest.of(page, size);
		Page<User> users = userOptionalFetcher.findUsersByPartialName(name, pageable);
		List<UserCabinetDto> userCabinetDtoList = new ArrayList<>();
		users.toList().forEach(user -> {
			BanHistory banHistory = userOptionalFetcher.findRecentActiveBanHistory(
					user.getUserId(), LocalDateTime.now());
			//todo : banhistory join으로 한번에 가능
			UserBlockedInfoDto blockedInfoDto = userMapper.toUserBlockedInfoDto(banHistory, user);
			Cabinet cabinet = cabinetOptionalFetcher.findLentCabinetByUserId(user.getUserId());
			CabinetDto cabinetDto = cabinetMapper.toCabinetDto(cabinet);
			userCabinetDtoList.add(cabinetMapper.toUserCabinetDto(blockedInfoDto, cabinetDto));
		});
		return cabinetMapper.toUserCabinetPaginationDto(userCabinetDtoList,
				users.getTotalElements());
	}

	@Override
	public List<User> getAllUsers() {
		log.debug("Called getAllUsers");
		return userOptionalFetcher.findAllUsers();
	}

	@Override
	public boolean checkUserExists(String name) {
		log.debug("Called checkUserExists: {}", name);
		return userService.checkUserExists(name);
	}

	@Override
	public void createUser(String name, String email, LocalDateTime blackholedAt, UserRole role) {
		log.debug("Called createUser: {}", name);
		userService.createUser(name, email, blackholedAt, role);
	}

	@Override
	public void createClubUser(String clubName) {
		log.debug("Called createClubUser: {}", clubName);
		userService.createClubUser(clubName);
	}

	@Override
	public boolean checkAdminUserExists(String email) {
		log.debug("Called checkAdminUserExists: {}", email);
		return userService.checkAdminUserExists(email);
	}

	@Override
	public void createAdminUser(String email) {
		log.debug("Called createAdminUser: {}", email);
		userService.createAdminUser(email);
	}

	@Override
	public void deleteUser(Long userId, LocalDateTime deletedAt) {
		log.debug("Called deleteUser: {}", userId);
		userService.deleteUser(userId, deletedAt);
	}

	@Override
	public void deleteAdminUser(Long adminUserId) {
		log.debug("Called deleteAdminUser: {}", adminUserId);
		userService.deleteAdminUser(adminUserId);
	}

	@Override
	public void updateAdminUserRole(Long adminUserId, AdminRole role) {
		log.debug("Called updateAdminUserRole: {}", adminUserId);
		userService.updateAdminUserRole(adminUserId, role);
	}

	@Override
	public void promoteUserToAdmin(String email) {
		log.debug("Called promoteUserToAdmin: {}", email);
		userService.promoteAdminByEmail(email);
	}

	@Override
	public void updateUserBlackholedAt(Long userId, LocalDateTime newBlackholedAt) {
		log.debug("Called updateUserBlackholedAt: {}", userId);
		userService.updateUserBlackholedAt(userId, newBlackholedAt);
	}

	@Override
	public void banUser(Long userId, LentType lentType, LocalDateTime startedAt,
			LocalDateTime endedAt,
			LocalDateTime expiredAt) {
		log.debug("Called banUser: {}", userId);
		userService.banUser(userId, lentType, startedAt, endedAt, expiredAt);
	}

	@Override
	public void deleteRecentBanHistory(Long userId, LocalDateTime today) {
		log.debug("Called deleteRecentBanHistory: {}", userId);
		userService.deleteRecentBanHistory(userId, today);
	}

	@Override
	public OverdueUserCabinetPaginationDto getOverdueUserList(Integer page, Integer size) {
		log.debug("Called getOverdueUserList");
		List<OverdueUserCabinetDto> overdueList = new ArrayList<>();
		// todo - size가 0일 때 모든 데이터를 가져오기
		if (size <= 0) {
			size = Integer.MAX_VALUE;
		}
		PageRequest pageable = PageRequest.of(page, size);
		lentOptionalFetcher.findAllOverdueLent(LocalDateTime.now(), pageable).stream().forEach(
				(lh) -> {
					User user = lh.getUser();
					Long overdueDays = DateUtil.calculateTwoDateDiff(LocalDateTime.now(),
							lh.getExpiredAt());
					Cabinet cabinet = lh.getCabinet();
					overdueList.add(
							cabinetMapper.toOverdueUserCabinetDto(lh, user,
									cabinet, overdueDays));
				}
		);
		return cabinetMapper.toOverdueUserCabinetPaginationDto(overdueList,
				Long.valueOf(overdueList.size()));
	}

	@Override
	public ClubUserListDto findAllClubUser(Integer page, Integer size) {
		log.debug("Called findAllClubUser");
		if (size <= 0) {
			size = Integer.MAX_VALUE;
		}
		PageRequest pageable = PageRequest.of(page, size);
		Page<User> pageUser = userOptionalFetcher.findClubUsers(pageable);
		List<UserProfileDto> userProfileDtos = pageUser
				.stream().map(u -> userMapper.toUserProfileDto(u)).collect(Collectors.toList());
		return userMapper.toClubUserListDto(userProfileDtos, pageUser.getTotalElements());
	}

	@Override
	public void deleteClubUser(Long userId) {
		log.debug("Called deleteClubUser");
		userService.deleteClubUser(userId, LocalDateTime.now());
	}

	@Override
	public void updateClubUser(Long clubId, String clubName) {
		log.debug("Called updateClubUser");
		userService.updateClubUser(clubId, clubName);
	}

	@Override
	public LentExtensionPaginationDto getAllLentExtension(Integer page, Integer size) {
		PageRequest pageable = PageRequest.of(page, size, Sort.by("expiredAt"));
		List<LentExtensionResponseDto> result =
				lentExtensionOptionalFetcher.findAllPaged(pageable).stream()
						.map(userMapper::toLentExtensionResponseDto).collect(Collectors.toList());
		return userMapper.toLentExtensionPaginationDto(result, (long) result.size());
	}

	@Override
	public LentExtensionPaginationDto getAllActiveLentExtension(Integer page, Integer size) {
		PageRequest pageable = PageRequest.of(page, size, Sort.by("expiredAt"));
		List<LentExtensionResponseDto> result =
				lentExtensionOptionalFetcher.findAllNotExpiredPaged(pageable).stream()
						.map(userMapper::toLentExtensionResponseDto).collect(Collectors.toList());
		return userMapper.toLentExtensionPaginationDto(result, (long) result.size());
	}

	@Override
	public LentExtensionPaginationDto getMyLentExtension(UserSessionDto userSessionDto) {
		log.debug("Called getMyLentExtension");
		List<LentExtensionResponseDto> result =
				lentExtensionOptionalFetcher.findAllByUserId(userSessionDto.getUserId())
						.stream()
						.sorted(Comparator.comparing(LentExtension::getExpiredAt))
						.map(userMapper::toLentExtensionResponseDto)
						.collect(Collectors.toList());
		return userMapper.toLentExtensionPaginationDto(result, (long) result.size());
	}

	@Override
	public LentExtensionPaginationDto getMyActiveLentExtensionPage(UserSessionDto userSessionDto) {
		log.debug("Called getMyActiveLentExtension");
		List<LentExtensionResponseDto> result = lentExtensionService.getActiveLentExtensionList(
				userSessionDto);
		return userMapper.toLentExtensionPaginationDto(result, (long) result.size());
	}

	@Override
	public void useLentExtension(UserSessionDto userSessionDto) {
		log.debug("Called useLentExtension");
		lentExtensionService.useLentExtension(userSessionDto.getUserId(), userSessionDto.getName());
	}

	@Transactional
	@Override
	public void updateAlarmState(UserSessionDto user, UpdateAlarmRequestDto dto) {
		log.debug("Called updateAlarmState");

		User findUser = userService.getUserWithAlarmOptIn(user.getUserId());
		Set<AlarmOptIn> alarmOptIns = findUser.getAlarmOptIns();
		List<AlarmType> currentAlarmTypes = alarmOptIns.stream().map(AlarmOptIn::getAlarmType)
				.collect(Collectors.toList());

		Map<AlarmType, Boolean> changedAlarmStatus = dto.getAlarmTypeStatus();
		alarmCommandService.updateAlarmStatus(findUser, currentAlarmTypes, changedAlarmStatus);
		alarmCommandService.updateAlarmStatusRe(dto, alarmQueryService.findAlarmStatusByUserId(
				user.getUserId()));
	}

}
