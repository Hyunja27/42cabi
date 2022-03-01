import schedule from 'node-schedule'
import { WebClient } from '@slack/web-api'
import { slackReturnUser } from '../../models/query'
import dotenv from 'dotenv'
import { slackUserList, slackUser } from '../../models/user';

const env = process.env;
if (process.env.USER === 'ec2-user') {
    if (env.PORT == '4242')
        dotenv.config({ path: '/home/ec2-user/git/42cabi/backend/.env' }); //dep
    else
        dotenv.config({ path: '/home/ec2-user/git/42cabi-dev/backend/.env' }); //dev
}
else
    dotenv.config(); //local
const slackBot = new WebClient(env.SLACK_TOKEN);
//반납일 전날 슬랙 메세지 발송
const sendReturnMsg = async () => {
    let date = new Date();
    date.setDate(date.getDate() + 1);
    let day = date.toISOString().replace(/T.+/, '');
    const message = `🗄 캐비닛 알림 🗄\n대여하신 캐비닛의 반납일은 ${day}일 내일입니다. 반납일 내 반납부탁드립니다.\n캐비닛 대여 서비스 바로가기  ➡️  https://cabi.42cadet.kr`;
    const intraList = await slackReturnUser(day);
    if (intraList) {
        try {
            for (let i = 0; i < intraList.length; i++) {
                const idx = slackUserList.findIndex((user: slackUser) => user.name == intraList[i]);
                if (idx === -1)
                    continue;
                await slackBot.chat.postMessage(
                    {
                        text: message,
                        channel: slackUserList[idx].id,
                    },
                );
            }
        } catch (error) {
            console.log(error);
            throw error;
        }
    }
};
//슬랙 user 리스트 생성
const slackUser = async () => {
    const res = await slackBot.users.list();
    if (res.members) {
        for (let i = 0; i < res.members.length; i++) {
            const element = res.members[i];
            if (element.id && element.name) {
                slackUserList.push({
                    id: element.id,
                    name: element.name
                })
            }
        };
    }
}
//대여 시 메세지 발송
export async function sendLentMsg(intra_id: string, expire_time: string) {
    const message = `🗄 캐비닛 알림 🗄\n대여하신 캐비닛의 반납일은 ${expire_time}일 입니다. 반납일 내 반납부탁드립니다.\n캐비닛 대여 서비스 바로가기  ➡️  https://cabi.42cadet.kr`;
    try {
        const idx = slackUserList.findIndex((user: slackUser) => user.name == intra_id);
        if (idx === -1)
            return ;
        await slackBot.chat.postMessage(
            {
                text: message,
                channel: slackUserList[idx].id,
            },
        );
    } catch (error) {
        console.log(error);
        throw error;
    }
};

export default async function slack() {
    await slackUser();
    const result = schedule.scheduleJob('0 9 * * *', function () {
        sendReturnMsg();
    });
}
