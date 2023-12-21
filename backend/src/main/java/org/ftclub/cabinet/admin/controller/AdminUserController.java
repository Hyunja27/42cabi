package org.ftclub.cabinet.admin.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ftclub.cabinet.auth.domain.AuthGuard;
import org.ftclub.cabinet.auth.domain.AuthLevel;
import org.ftclub.cabinet.dto.ClubUserListDto;
import org.ftclub.cabinet.dto.LentExtensionPaginationDto;
import org.ftclub.cabinet.dto.LentHistoryPaginationDto;
import org.ftclub.cabinet.lent.newService.LentFacadeService;
import org.ftclub.cabinet.user.service.LentExtensionService;
import org.ftclub.cabinet.user.service.UserFacadeService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자가 유저를 관리할 때 사용하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v4/admin/users")
@Log4j2
public class AdminUserController {

	private final UserFacadeService userFacadeService;
	private final LentFacadeService lentFacadeService;
	private final LentExtensionService lentExtensionService;

	/**
	 * 현재 유저가 차단된 상태일 때, 차단을 해제합니다.
	 *
	 * @param userId 유저 고유 아이디
	 */
	@DeleteMapping("/{userId}/ban-history")
	@AuthGuard(level = AuthLevel.ADMIN_ONLY)
	public void deleteBanHistoryByUserId(@PathVariable("userId") Long userId) {
		log.info("Called deleteBanHistoryByUserId: {}", userId);
		userFacadeService.deleteRecentBanHistory(userId, LocalDateTime.now());
	}

	/**
	 * 유저의 대여 기록을 반환합니다.
	 *
	 * @param userId      유저 고유 아이디
	 * @param pageRequest 페이지네이션 정보
	 * @return {@link LentHistoryPaginationDto} 유저의 대여 기록
	 */
	@GetMapping("/{userId}/lent-histories")
	@AuthGuard(level = AuthLevel.ADMIN_ONLY)
	public LentHistoryPaginationDto getLentHistoriesByUserId(@PathVariable("userId") Long userId,
			@RequestBody PageRequest pageRequest) {
		log.info("Called getLentHistoriesByUserId: {}", userId);
		return lentFacadeService.getUserLentHistories(userId, pageRequest);
	}

	/**
	 * 유저를 어드민으로 승격시킵니다.
	 *
	 * @param email 유저 이메일
	 * @return redirect:cabi.42seoul.io/admin/login
	 */
	@GetMapping("/admins/promote")
	@AuthGuard(level = AuthLevel.MASTER_ONLY)
	public void promoteUserToAdmin(@RequestParam("email") String email) {
		log.info("Called promoteUserToAdmin: {}", email);
		userFacadeService.promoteUserToAdmin(email);
	}

	/**
	 * 동아리 유저를 생성합니다.
	 *
	 * @param body
	 */
	@PostMapping("/club")
	@AuthGuard(level = AuthLevel.ADMIN_ONLY)
	public void createClubUser(@RequestBody HashMap<String, String> body) {
		log.info("Called createClub");
		String clubName = body.get("clubName");
		userFacadeService.createClubUser(clubName);
	}

	/**
	 * 동아리 유저를 삭제합니다.
	 *
	 * @param clubId 동아리 고유 아이디
	 */
	@DeleteMapping("/club/{clubId}")
	@AuthGuard(level = AuthLevel.ADMIN_ONLY)
	public void deleteClubUser(@PathVariable("clubId") Long clubId) {
		log.info("Called deleteClub");
		userFacadeService.deleteClubUser(clubId);
	}

	@GetMapping("/clubs")
	@AuthGuard(level = AuthLevel.ADMIN_ONLY)
	public ClubUserListDto findClubs(@RequestParam("page") Integer page,
			@RequestParam("size") Integer size) {
		log.info("Called getClubs");
		return userFacadeService.findAllClubUser(page, size);
	}

	@PatchMapping("/club/{clubId}")
	@AuthGuard(level = AuthLevel.ADMIN_ONLY)
	public void updateClubUser(@PathVariable("clubId") Long clubId,
			@RequestBody HashMap<String, String> body) {
		log.info("Called updateClub");
		String clubName = body.get("clubName");
		userFacadeService.updateClubUser(clubId, clubName);
	}

	@GetMapping("/lent-extensions")
	@AuthGuard(level = AuthLevel.ADMIN_ONLY)
	public LentExtensionPaginationDto getAllLentExtension(@RequestParam("page") Integer page,
			@RequestParam("size") Integer size) {
		log.info("Called getAllLentExtension");
		return userFacadeService.getAllLentExtension(page, size);
	}

	@GetMapping("/lent-extensions/active")
	@AuthGuard(level = AuthLevel.ADMIN_ONLY)
	public LentExtensionPaginationDto getAllActiveLentExtension(@RequestParam("page") Integer page,
			@RequestParam("size") Integer size) {
		log.info("Called getAllActiveLentExtension");
		return userFacadeService.getAllActiveLentExtension(page, size);
	}

	@PostMapping("/lent-extensions/{user}")
	@AuthGuard(level = AuthLevel.ADMIN_ONLY)
	public void issueLentExtension(@PathVariable("user") String username) {
		log.info("Called issueLentExtension");
		lentExtensionService.assignLentExtension(username);

	}
}
