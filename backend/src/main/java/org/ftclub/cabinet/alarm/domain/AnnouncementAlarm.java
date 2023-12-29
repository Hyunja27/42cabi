package org.ftclub.cabinet.alarm.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 공지사항 알람
 */
@Getter
@AllArgsConstructor
public class AnnouncementAlarm implements Alarm, TransactionalAlarmEvent {

	private final String announcementContent;
}
