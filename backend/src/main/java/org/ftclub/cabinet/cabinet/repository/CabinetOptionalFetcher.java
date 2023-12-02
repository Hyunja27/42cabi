package org.ftclub.cabinet.cabinet.repository;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ftclub.cabinet.cabinet.domain.Cabinet;
import org.ftclub.cabinet.cabinet.domain.CabinetStatus;
import org.ftclub.cabinet.cabinet.domain.LentType;
import org.ftclub.cabinet.dto.ActiveCabinetInfoEntities;
import org.ftclub.cabinet.exception.ExceptionStatus;
import org.ftclub.cabinet.exception.ServiceException;
import org.ftclub.cabinet.mapper.CabinetMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CabinetService를 위한 ExceptionService
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CabinetOptionalFetcher {

	private final CabinetRepository cabinetRepository;
	private final CabinetMapper cabinetMapper;

	/*-------------------------------------------FIND-------------------------------------------*/

	public Cabinet findCabinet(Long cabinetId) {
		log.debug("Called findCabinet: {}", cabinetId);
		return cabinetRepository.findById(cabinetId).orElse(null);
	}

	public List<ActiveCabinetInfoEntities> findCabinetsActiveLentHistoriesByBuildingAndFloor(
			String building, Integer floor) {
		return cabinetRepository.findCabinetsActiveLentHistoriesByBuildingAndFloor(building, floor);
	}

	/**
	 * 유저 ID로 사물함을 찾습니다.
	 *
	 * @param userId 유저ID
	 * @return 사물함 엔티티
	 * @throws ServiceException 사물함을 찾을 수 없는 경우
	 */
	public Cabinet findLentCabinetByUserId(Long userId) {
		log.debug("Called findLentCabinetByUserId: {}", userId);
		return cabinetRepository.findByUserIdAndEndedAtIsNull(userId).orElse(null);
	}

	public List<String> findAllBuildings() {
		log.debug("Called findAllBuildings");
		return cabinetRepository.findAllBuildings();
	}

	public List<Integer> findAllFloorsByBuilding(String building) {
		log.debug("Called findAllFloorsByBuilding: {}", building);
		return cabinetRepository.findAllFloorsByBuilding(building);
	}

	public List<Cabinet> findAllPendingCabinetsByCabinetStatusAndBeforeEndedAt(
			CabinetStatus cabinetStatus,
			LocalDateTime currentDate) {
		log.debug("Called findAllCabinetsByCabinetStatusAndBeforeEndedAt: {}, {}",
				cabinetStatus, currentDate);
		return cabinetRepository.findAllCabinetsByCabinetStatusAndBeforeEndedAt(cabinetStatus,
				currentDate);
	}

	public Page<Cabinet> findPaginationByLentType(LentType lentType, PageRequest pageable) {
		log.debug("Called findPaginationByLentType: {}", lentType);
		return cabinetRepository.findPaginationByLentType(lentType, pageable);
	}

	public Page<Cabinet> findPaginationByStatus(CabinetStatus status, PageRequest pageable) {
		log.debug("Called findPaginationByStatus: {}", status);
		return cabinetRepository.findPaginationByStatus(status, pageable);
	}

	public Page<Cabinet> findPaginationByVisibleNum(Integer visibleNum, PageRequest pageable) {
		log.debug("Called findPaginationByVisibleNum: {}", visibleNum);
		return cabinetRepository.findPaginationByVisibleNum(visibleNum, pageable);
	}

	public List<Cabinet> findAllCabinetsByBuildingAndFloor(String building, Integer floor) {
		return cabinetRepository.findAllByBuildingAndFloorOrderByVisibleNum(building, floor);
	}
	/*-------------------------------------------GET--------------------------------------------*/


	/**
	 * 사물함 ID로 변경 사항이 예정된 사물함을 찾습니다.
	 * <p>
	 * X Lock을 획득한 상태로 가져옵니다.
	 *
	 * @param cabinetId 사물함 ID
	 * @return 사물함 엔티티
	 * @throws ServiceException 사물함을 찾을 수 없는 경우
	 */
	public Cabinet getCabinetForUpdate(Long cabinetId) {
		log.debug("Called getCabinetForUpdate: {}", cabinetId);
		return cabinetRepository.findByCabinetIdForUpdate(cabinetId)
				.orElseThrow(() -> new ServiceException(ExceptionStatus.NOT_FOUND_CABINET));
	}

	/**
	 * 사물함 ID로 사물함을 찾습니다.
	 *
	 * @param cabinetId 사물함 ID
	 * @return 사물함 엔티티
	 * @throws ServiceException 사물함을 찾을 수 없는 경우
	 */
	public Cabinet getCabinet(Long cabinetId) {
		log.debug("Called getCabinet: {}", cabinetId);
		return cabinetRepository.findById(cabinetId)
				.orElseThrow(() -> new ServiceException(ExceptionStatus.NOT_FOUND_CABINET));
	}

	/**
	 * 유저 ID로 사물함을 찾습니다.
	 *
	 * @param userId 유저ID
	 * @return 사물함 엔티티
	 * @throws ServiceException 사물함을 찾을 수 없는 경우
	 */
	public Cabinet getLentCabinetByUserId(Long userId) {
		log.debug("Called getLentCabinetByUserId: {}", userId);
		return cabinetRepository.findByUserIdAndEndedAtIsNull(userId)
				.orElseThrow(() -> new ServiceException(ExceptionStatus.NOT_FOUND_CABINET));
	}

	/**
	 * 사물함 ID로 동아리 사물함을 찾습니다.
	 *
	 * @param cabinetId 사물함 ID
	 * @return 동아리 사물함 엔티티
	 * @throws ServiceException 사물함을 찾을 수 없는 경우
	 */
	public Cabinet getClubCabinet(Long cabinetId) {
		log.debug("Called getClubCabinet: {}", cabinetId);
		Cabinet cabinet = cabinetRepository.findById(cabinetId)
				.orElseThrow(() -> new ServiceException(ExceptionStatus.NOT_FOUND_CABINET));
		if (!cabinet.isLentType(LentType.CLUB)) {
			throw new ServiceException(ExceptionStatus.NOT_FOUND_CABINET);
		}
		return cabinet;
	}

	public List<Cabinet> findPendingCabinets(
			String building, LentType lentType, List<CabinetStatus> cabinetStatuses) {
		return cabinetRepository.findAllByBuildingAndLentTypeNotAndStatusIn(
				building, lentType, cabinetStatuses);
	}
}
