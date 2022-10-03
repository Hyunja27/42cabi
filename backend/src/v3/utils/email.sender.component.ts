import { MailerService } from '@nestjs-modules/mailer';
import { Inject, Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { UserOverDto } from 'src/dto/user.over.dto';

@Injectable()
export class EmailSender {
  private logger = new Logger(EmailSender.name);
  private emailTest: boolean;
  constructor(
    @Inject(ConfigService) private configService: ConfigService,
    private readonly mailerService: MailerService,
  ) {
    this.emailTest = configService.get<boolean>('email.test');
  }

  // TODO: 일단 원래 기능 그대로 가져왔으나 어디서 호출하고 어떻게 쓰이냐에 따라 수정해야할 것 같습니다!
  public sendEmail(intra_id: string, subject: string, file: string): void {
    const emailFrom = this.configService.get<string>('email.from');
    this.mailerService
      .sendMail({
        from: `42CABI <${emailFrom}>`,
        to: intra_id + '@student.42seoul.kr',
        subject,
        template: `./${file}`,
        context: { intra_id },
      })
      .then((success) => {
        this.logger.log(`Send mail to ${intra_id} success!`);
        this.logger.log(`${intra_id} : ${new Date()} : ${success.response}`);
      })
      .catch((e) => {
        this.logger.error(`Send mail to ${intra_id} failed.. 🥺 ${e}`);
        this.logger.error(`${intra_id} : ${new Date()} : ${e}`);
      });
  }

  public mailing(info: UserOverDto[], num: number) {
    let subject = '42CABI 사물함 연체 알림';
    let file;
    if (num === 0) {
      file = 'soonoverdue.hbs';
    } else if (num === 7) {
      file = 'overdue.hbs';
    } else if (num === 14) {
      file = 'lastoverdue.hbs';
    } else if (num === 15) {
      subject = '42CABI 강제 반납 안내';
      file = 'forcedreturn.hbs';
    }
    // 배포 시에만 메일 발송 환경변수 확인
    if (this.emailTest === false) {
      info.forEach((user) => {
        this.sendEmail(user.intra_id, subject, file);
      });
    } else {
      info.forEach((user) => {
        this.logger.debug(`[TESTING] [${subject}], sentTo: ${user.intra_id}`);
      });
    }
  }
}
