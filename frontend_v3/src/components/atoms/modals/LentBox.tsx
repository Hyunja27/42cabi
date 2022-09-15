import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import FormGroup from "@mui/material/FormGroup";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import { axiosLent } from "../../../network/axios/axios.custom";
import CheckButton from "../buttons/CheckButton";

const BoxStyle = {
  position: "fixed" as const,
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: "24rem",
  bgcolor: "background.paper",
  border: 0,
  borderRadius: "1rem",
  outline: 0,
  boxShadow: 16,
  p: 4,
};

const CenterAlignStyle = {
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
};

// TODO:
// cabinetInfo는 API로 받아와야 함
const cabinetInfo = {
  cabinet_id: 0,
  cabinet_num: 0,
  location: "새롬관",
  floor: 2,
  section: "OA",
  activation: false,
};

// XXX: cabinet_id, isLentAble
// cabinet_id: 모달이 띄워지는 시점에 사용자가 누른 사물함의 id입니다.
// 이것도 리덕스에 저장해서 사용해야 할까요? 아니면 props로 넘겨주는 게 나을까요?
// isLentable: 현재 접속한 사용자가 새 사물함을 대여가능한 상태인지 여부를 나타내는 값입니다.
// 리덕스에 저장 후 Selector로 가져와서 사용하게 될 것 같습니다.
interface LentBoxProps {
  // eslint-disable-next-line react/require-default-props
  handleClose: () => void;
  cabinet_id: number;
  isLentAble: boolean;
}

const LentBox = (props: LentBoxProps): JSX.Element => {
  const { handleClose, isLentAble, cabinet_id } = props;
  const [isChecked, setIsChecked] = useState(false);
  const navigate = useNavigate();
  const handleCheckClick = (): void => {
    setIsChecked(!isChecked);
  };

  const handleLent = (): void => {
    axiosLent(cabinet_id)
      .then(() => {
        navigate("/Lent");
      })
      .catch((error) => {
        console.error(error);
        alert("🚨 대여에 실패했습니다 🚨");
      });
    handleClose();
  };

  const LentAble: JSX.Element = (
    <Box sx={BoxStyle}>
      <Typography id="modal-modal-title" variant="h6" component="h2">
        [{cabinetInfo.cabinet_num}]번 사물함을 대여합니다.
      </Typography>
      <Typography id="modal-modal-description" sx={{ mt: 2 }} align="center">
        대여기간은 +30일 입니다.
      </Typography>
      <Typography id="modal-modal-description" sx={{ mt: 2 }} align="center">
        이용 중 귀중품 분실에 책임지지 않습니다.
      </Typography>
      <FormGroup sx={CenterAlignStyle}>
        <FormControlLabel
          control={<Checkbox onClick={handleCheckClick} />}
          label="알겠습니다. 대여할게요!"
        />
        <div>
          <CheckButton
            color="secondary"
            variant="contained"
            content="취소"
            onClick={handleClose}
          />
          <CheckButton
            color="primary"
            variant="contained"
            content="대여"
            isChecked={isChecked}
            onClick={handleLent}
          />
        </div>
      </FormGroup>
    </Box>
  );

  const LentUnableContent = "이미 대여중인 사물함이 있어요 :)";
  const LentUnable: JSX.Element = (
    <Box sx={BoxStyle}>
      <Typography
        id="modal-modal-title"
        variant="h6"
        component="h2"
        align="center"
      >
        {LentUnableContent}
      </Typography>
      <div style={CenterAlignStyle}>
        <CheckButton
          color="primary"
          variant="contained"
          content="확인"
          onClick={handleClose}
        />
      </div>
    </Box>
  );

  return <div>{isLentAble ? LentAble : LentUnable}</div>;
};

LentBox.defaultProps = {
  // eslint-disable-next-line react/default-props-match-prop-types
  handleClose: () => {
    console.log("closed");
  },
};

export default LentBox;
