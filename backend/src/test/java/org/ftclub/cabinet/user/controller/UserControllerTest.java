package org.ftclub.cabinet.user.controller;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Disabled
public class UserControllerTest {

//	@Autowired
//	MockMvc mockMvc;
//
//	@Autowired
//	JwtProperties jwtProperties;
//
//	@Test
//	public void testGetMyProfile_대여_사물함_없는_경우() throws Exception {
//		// penaltyuser2 대여 중인 사물함 x 벤 기록 x
//		MyProfileResponseDto myProfileResponseDto = new MyProfileResponseDto(4L, "penaltyuser2",
//				null, null, null);
//
//		String userToken = TestUtils.getTestUserTokenByName(jwtProperties.getSigningKey(),
//				LocalDateTime.now(), DateUtil.getInfinityDate(), "penaltyuser2", "user.domain.com");
//		Cookie cookie = TestUtils.getTokenCookie("사용자", userToken);
//
//		mockMvc.perform(mockRequest(HttpMethod.GET, cookie,
//						"/v4/users/me"))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.userId").value(myProfileResponseDto.getUserId()))
//				.andExpect(jsonPath("$.name").value(myProfileResponseDto.getName()))
//				.andExpect(jsonPath("$.cabinetId").value(myProfileResponseDto.getCabinetId()))
//				.andExpect(jsonPath("$.unbannedAt").value(myProfileResponseDto.getUnbannedAt()));
//	}
//
//	@Test
//	public void testGetMyProfile_대여_사물함_있는_경우() throws Exception {
//		// lentuser1 대여 중인 사물함 3번
//		MyProfileResponseDto myProfileResponseDto = new MyProfileResponseDto(5L, "lentuser1",
//				3L, null, null);
//
//		String userToken = TestUtils.getTestUserTokenByName(jwtProperties.getSigningKey(),
//				LocalDateTime.now(), DateUtil.getInfinityDate(), "lentuser1", "user.domain.com");
//		Cookie cookie = TestUtils.getTokenCookie("사용자", userToken);
//		mockMvc.perform(mockRequest(HttpMethod.GET, cookie,
//						"/v4/users/me"))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.userId").value(myProfileResponseDto.getUserId()))
//				.andExpect(jsonPath("$.name").value(myProfileResponseDto.getName()))
//				.andExpect(jsonPath("$.cabinetId").value(myProfileResponseDto.getCabinetId()))
//				.andExpect(jsonPath("$.unbannedAt").value(myProfileResponseDto.getUnbannedAt()));
//	}
}

