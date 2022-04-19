import axios from 'axios'
import React, { useState, useEffect } from 'react'
import { useHistory } from 'react-router-dom'
import { userInfo } from './Main'
import Menu from '../component/Menu'
import Carousel from '../component/Carousel'
import Location from '../component/Location'
import LentModal from '../modal/LentModal'
import ContentsModal from '../modal/ContentsModal'
import EventModal from '../modal/EventModal'
import './lent.css'
import './main.css'

export type cabinetInfo = {
  cabinet_id: number,
  cabinet_num: number,
  location: string,
  floor: number,
  section: string,
  activation: boolean,
}
export type locationInfo = {
  location?: Array<string>,
  floor?: Array<Array<number>>,
  section?: Array<Array<Array<string>>>,
  cabinet?: Array<Array<Array<Array<cabinetInfo>>>>
}
export type lentInfo = {
  lent_id: number,
  lent_cabinet_id: number,
  lent_user_id: number,
  lent_time?: string,
  expire_time?: string,
  extension: boolean,
  intra_id?: string
}

export default function Lent() {
  const history = useHistory();
  const [user, setUser] = useState<userInfo>();
  const [l_idx, setLidx] = useState<number>(0);
  const [isLent, setisLent] = useState<number>(0);
  const [target, setTarget] = useState<number>(-1);
  const [cabiNum, setCabiNum] = useState<number>(-1);
  const [info, setInfo] = useState<locationInfo>({});
  const [lent, setLent] = useState<Array<lentInfo>>([]);

  useEffect(() => {
    apiCheck(); // 적절한 유저가 페이지를 접근하는지에 대한 정보
    if (!info.location) {
      handleClick();
    }
    handleLent();
  }, [l_idx, info]);  // l_idx나 info가 바뀔 때마다 호출됨

  const apiCheck = async () => {
    const url = "/api/check"  // 적절한 유저가 페이지를 접근하는지에 대한 정보
    await axios.post(url)
      .then((res: any) => {
        setUser(res.data.user);
      }).catch((err: any) => {
        console.log(err);
        history.push("/");  // 에러 발생시 메인화면으로 돌아감
      });
  };

  const handleHome = () => {
    history.go(0);
  }

  const handleLent = () => {
    const url = "/api/lent_info"  // 현재 모든 대여자들의 정보
    axios.post(url).then((res: any) => {
      setLent(res.data.lentInfo);
      setisLent(res.data.isLent);
    }).catch((err) => { console.log(err) });
  };

  const handleClick = () => {
    const url = "/api/cabinet"  // 전체 사물함에 대한 정보
    axios.post(url).then((res: any) => {
      setInfo(res.data);  // locationInfo 가져옴
    }).catch((err) => { console.log(err) });
  };

  const navTabs = () => {
    let list: Array<JSX.Element> = [];

    if (!info || !info.floor || info.floor.length <= l_idx) {
      return list;
    }
    info.floor[l_idx].forEach((floor: number, idx: number) => {
      list.push(
        <button className={`nav-link px-4${idx ? "" : " active"}`}
          id={`nav-tab`} key={`nav-${floor}-tab`} data-bs-toggle="tab" data-bs-target={`#nav-${floor}`}
          type="button" role="tab" aria-controls={`nav-${floor}`} aria-selected={idx ? "false" : "true"}>
          {floor}
        </button>
      )
    });
    return list;
  };

  const navContent = () => {
    let list: Array<JSX.Element> = [];

    if (!info || !info.floor || info.floor.length <= l_idx) {
      return list;
    }
    info.floor[l_idx].forEach((floor: number, idx: number) => {
      list.push(
        <Carousel setTarget={setTarget} setCabiNum={setCabiNum} info={info} user={user?.intra_id} l_idx={l_idx} outer_i={idx} outer_lent={lent} floor_name={floor} isLent={isLent} lent={lent}></Carousel>
      );
    })
    return list;
  };
  const eventModal = () => {
    if (new Date() < new Date(2022, 3, 18, 10, 0, 0)) {
      return null;
    } else if (localStorage.getItem("returnEventShown")) {
      return null;
    }
    return <EventModal />
  }

  return (
    <div className="container col" id="container">
      {
        eventModal()
      }
      <div className="row align-items-center">
        <div className="col"><div className="px-4"><img src="../img/cabinet.ico" onClick={handleHome} width="30" /></div></div>
        <div className="col">
          <Location info={info} l_idx={l_idx} setLidx={setLidx}></Location>
        </div>
        <div className="col">
          <Menu url="/return"></Menu>
        </div>
      </div>
      <div className="row my-2 mx-2" >
        <nav>
          <div className="nav nav-tabs border-bottom-0" id="nav-tabs" role="tablist">
            <React.Fragment>{navTabs()}</React.Fragment>
          </div>
        </nav>
        <div className="tab-content" id="nav-tabContent">
          <React.Fragment>{navContent()}</React.Fragment>
        </div>
      </div>
      <LentModal target={target} cabiNum={cabiNum}></LentModal>
      <ContentsModal contents="이미 대여중인 사물함이 있어요 :)" />
    </div>
  );
}
