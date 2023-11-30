package org.ftclub.cabinet.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ftclub.cabinet.cabinet.domain.Cabinet;
import org.ftclub.cabinet.cabinet.repository.CabinetOptionalFetcher;
import org.ftclub.cabinet.config.CabinetProperties;
import org.ftclub.cabinet.dto.LentExtensionResponseDto;
import org.ftclub.cabinet.dto.UserMonthDataDto;
import org.ftclub.cabinet.dto.UserSessionDto;
import org.ftclub.cabinet.exception.ExceptionStatus;
import org.ftclub.cabinet.exception.ServiceException;
import org.ftclub.cabinet.lent.domain.LentHistory;
import org.ftclub.cabinet.lent.repository.LentOptionalFetcher;
import org.ftclub.cabinet.mapper.UserMapper;
import org.ftclub.cabinet.occupiedtime.OccupiedTimeManager;
import org.ftclub.cabinet.user.domain.LentExtension;
import org.ftclub.cabinet.user.domain.LentExtensionPolicy;
import org.ftclub.cabinet.user.domain.LentExtensionType;
import org.ftclub.cabinet.user.domain.LentExtensions;
import org.ftclub.cabinet.user.repository.LentExtensionOptionalFetcher;
import org.ftclub.cabinet.user.repository.LentExtensionRepository;
import org.ftclub.cabinet.user.repository.UserOptionalFetcher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class LentExtensionServiceImpl implements LentExtensionService {

	private final LentExtensionRepository lentExtensionRepository;
	private final LentExtensionOptionalFetcher lentExtensionOptionalFetcher;
	private final LentOptionalFetcher lentOptionalFetcher;
	private final UserOptionalFetcher userOptionalFetcher;
	private final CabinetProperties cabinetProperties;
	private final OccupiedTimeManager occupiedTimeManager;
	private final UserMapper userMapper;
	private final CabinetOptionalFetcher cabinetOptionalFetcher;
	private final LentExtensionPolicy lentExtensionPolicy;

	@Override
	@Scheduled(cron = "${spring.schedule.cron.extension-issue-time}")
	public void issueLentExtension() {
		log.debug("Called issueLentExtension");
		List<UserMonthDataDto> userMonthDataDtos = occupiedTimeManager.filterToMetUserMonthlyTime(
				occupiedTimeManager.getUserLastMonthOccupiedTime());
		LocalDateTime now = LocalDateTime.now();
		userMonthDataDtos.stream().forEach(userMonthDataDto -> {
			LentExtension lentExtension = LentExtension.of("lentExtension",
					cabinetProperties.getLentExtendTerm(),
					LocalDateTime.of(now.getYear(), now.getMonth(),
							now.getMonth().length(now.toLocalDate().isLeapYear()), 23, 59, 0),
					LentExtensionType.ALL,
					userOptionalFetcher.findUserByName(userMonthDataDto.getLogin()).getUserId());
			lentExtensionRepository.save(lentExtension);
		});
	}


	@Override
	public void assignLentExtension(String username) {
		log.debug("Called assignLentExtension {}", username);
		LocalDateTime now = LocalDateTime.now();

		LentExtension lentExtension = LentExtension.of("lentExtension",
				cabinetProperties.getLentExtendTerm(),
				LocalDateTime.of(now.getYear(), now.getMonth(),
						now.getMonth().length(now.toLocalDate().isLeapYear()), 23, 59, 0),
				LentExtensionType.ALL,
				userOptionalFetcher.findUserByName(username).getUserId());
		lentExtensionRepository.save(lentExtension);
	}

	@Override
	public List<LentExtensionResponseDto> getActiveLentExtensionList(
			UserSessionDto userSessionDto) {
		log.debug("Called getLentExtensionList {}", userSessionDto.getName());

		LentExtensions lentExtensions = LentExtensions.builder()
				.lentExtensions(lentExtensionOptionalFetcher.findAllByUserIdUsedAtIsNull(
						userSessionDto.getUserId())).build();

		return lentExtensions.getLentExtensions().stream()
				.map(userMapper::toLentExtensionResponseDto).collect(Collectors.toList());
	}

	@Override
	public LentExtensionResponseDto getActiveLentExtension(UserSessionDto userSessionDto) {
		LentExtensionResponseDto lentExtensionResponseDto = null;
		List<LentExtension> activeLentExtensionsByUserId = lentExtensionOptionalFetcher.findAllByUserIdUsedAtIsNull(
				userSessionDto.getUserId());
		LentExtensions lentExtensions = LentExtensions.builder()
				.lentExtensions(activeLentExtensionsByUserId).build();
		if (lentExtensions.hasActiveLentExtensions()) {
			lentExtensionResponseDto = userMapper.toLentExtensionResponseDto(
					lentExtensions.getImminentActiveLentExtension());
		}
		return lentExtensionResponseDto;
	}

	@Override
	public void useLentExtension(Long userId, String username) {
		log.debug("Called useLentExtension {}", username);

		List<LentExtension> findLentExtension =
				lentExtensionOptionalFetcher.findAllByUserId(userId);

		LentExtensions lentExtensions = LentExtensions.builder().lentExtensions(findLentExtension)
				.build();
		if (!lentExtensions.hasActiveLentExtensions()) {
			throw new ServiceException(ExceptionStatus.EXTENSION_NOT_FOUND);
		}

		Cabinet cabinet = cabinetOptionalFetcher.getLentCabinetByUserId(userId);
		List<LentHistory> activeLentHistories = lentOptionalFetcher.findActiveLentHistoriesByCabinetId(
				cabinet.getCabinetId());
		lentExtensionPolicy.verifyLentExtension(cabinet, activeLentHistories);

		LentExtension lentExtension = lentExtensions.getImminentActiveLentExtension();
		lentExtension.use();
		// 연장
		activeLentHistories
				.forEach(lentHistory -> lentHistory.setExpiredAt(
						lentHistory.getExpiredAt().plusDays(lentExtension.getExtensionPeriod())));
	}

}
