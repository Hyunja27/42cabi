import schedule from 'node-schedule';
// const schedule = require('node-schedule');
// const mailer = require('nodemailer');
// const fs = require('fs');
import fs from 'fs';
import mailer from 'nodemailer';
import { createLentLog } from '../../models/queryModel';
import { overUserInfo } from '../../models/types';
import { addBanUser, getOverUser, updateCabinetActivation, updateUserAuth } from '../../models/banModel';
require('dotenv').config();

let transporter = mailer.createTransport({
  service: process.env.MAIL_SERVICE,
  host: process.env.MAIL_HOST,
  auth: {
    user: process.env.MAIL_USER,
    pass: process.env.MAIL_PASS,
  },
  secure: true,
  port: 465,
});

//sending mail to person with email
const sendMail = (email: string, subject: string, file: string) => {
  let mailOptions = {
    from: `42CABI <${process.env.MAIL_FROM}>`,
    to: email,
    subject: subject, //'42CABI 사물함 연체 알림'
    text: fs.readFileSync(file, 'utf8'),
  };

  transporter.sendMail(mailOptions, (err: any, info:any) => {
    if (err) {
      console.log(err)
    } else {
      console.log(email + ': ' + info.response);
    }
  });
}

const mailing = (info: overUserInfo[], num: number) => {
  const fortytwo = '@student.42seoul.kr';
  let subject = '42CABI 사물함 연체 알림';
  let file = '../../email/overdue.txt';
  if (num === 0) {
    file = '../../email/soonoverdue.txt';
  } else if (num === 7) {
    file = '../../email/overdue.txt';
  } else if (num === 14) {
    file = '../../email/lastoverdue.txt';
  } else if (num === 15) {
    subject = '42CABI 영구사용정지 안내';
    file = '../../email/ban.txt';
  }
  info.forEach(user => sendMail(user.intra_id + fortytwo, subject, file));
}

const scheduling = () => {
  const rule = new schedule.RecurrenceRule();
  rule.dayOfWeek = [0, new schedule.Range(0, 6)];
  rule.hour = 9;
  rule.minute = 0;
  
  const result = schedule.scheduleJob(rule, async () => {
    try {
      const dayList = [0, 7, 14];
      dayList.forEach(day => {
        getOverUser(day).then(res => {
          if (res) {
            mailing(res, day);
          }
        }).catch(e => console.error(e));
      })
      //15
      getOverUser(15).then(res => {
        if (res) {
          res.forEach(async user => {
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
          mailing(res, 15);
        }
      }).catch(e => console.error(e));
    } catch(e) {
      console.error(e);
      throw e;
    }
  });
}

export default scheduling;