import { MailerService } from '@nestjs-modules/mailer';
import { Injectable, Logger } from '@nestjs/common';
import { Cron, CronExpression } from '@nestjs/schedule';
import fs from 'fs';

@Injectable()
export class MailService {
  private logger = new Logger('EmailService');
  constructor(private readonly mailerService: MailerService) {}

  public sendMail(intra_id: string, subject: string, file: string): void {
    this.mailerService
      .sendMail({
        from: `"42CABI" <${process.env.MAIL_FROM}>`,
        to: intra_id + '@student.42seoul.kr',
        subject: subject,
        template: `./${file}`,
        context: { intra_id },
      })
      .then((success) => {
        this.logger.log(`Send mail to ${intra_id} success! ${success}`);
        fs.appendFileSync(
          './email_logs/emailLog.txt',
          `${intra_id} : ${new Date()} : ${success.response}`,
        );
      })
      .catch((err) => {
        this.logger.error(`Send mail to ${intra_id} failed.. 🥺 ${err}`);
        fs.appendFileSync(
          './email_logs/emailLog.txt',
          `${intra_id} : ${new Date()} : ${err}`,
        );
      });
  }

  public mailing(info: overUserInfo[], num: number) {
    let subject = '42CABI 사물함 연체 알림';
    let file = 'overdue.hbs';
    if (num === 0) {
      file = 'soonoverdue.hbs';
    } else if (num === 7) {
      file = 'overdue.hbs';
    } else if (num === 14) {
      file = 'lastoverdue.hbs';
    } else if (num === 15) {
      subject = '42CABI 영구사용정지 안내';
      file = 'ban.hbs';
    }
    info.forEach((user) => this.sendMail(user.intra_id, subject, file));
  }

  @Cron(CronExpression.EVERY_DAY_AT_9PM)
  public scheduling() {
    this.logger.log('연체된 사용자들에게 메일을 보내는 중...');
    const dayList = [0, 7, 14];
    dayList.forEach((day) => {
      getOverUser(day)
        .then((res) => {
          if (res) {
            this.mailing(res, day);
          }
        })
        .catch((e) => this.logger.error(e));
    });
    //15
    // ban 유저 관련 db작업은 따로 빼는게 좋을 것 같습니다.
    getOverUser(15)
      .then((res) => {
        if (res) {
          res.forEach(async (user) => {
            //user
            await updateUserAuth(user.user_id);
            //cabinet
            await updateCabinetActivation(user.cabinet_id, 2);
            //return
            await createLentLog({
              user_id: user.user_id,
              intra_id: user.intra_id,
            });
            //ban
            await addBanUser({
              user_id: user.user_id,
              intra_id: user.intra_id,
              cabinet_id: user.cabinet_id,
            });
          });
          this.mailing(res, 15);
          connectionForCabinet();
        }
      })
      .catch((e) => this.logger.error(e));
  }
}
