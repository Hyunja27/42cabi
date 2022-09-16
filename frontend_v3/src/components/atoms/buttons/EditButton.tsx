import { SetStateAction, Dispatch } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faPenToSquare,
  faCircleCheck,
} from "@fortawesome/free-regular-svg-icons";
import styled from "@emotion/styled";
import {
  axiosUpdateCabinetMemo,
  axiosUpdateCabinetTitle,
} from "../../../network/axios/axios.custom";

const Button = styled.button`
  display: flex;
  justify-content: center;
  align-items: center;
  width: 2rem;
  height: 2rem;
  padding: 0;
  background-color: transparent;
  &,
  &:focus,
  &:hover {
    border: 0;
    outline: 0;
  }
`;

interface EditButtonProps {
  contentType: "title" | "memo";
  isToggle: boolean;
  setIsToggle: Dispatch<SetStateAction<boolean>>;
  inputValue: string;
  setTextValue: Dispatch<SetStateAction<string>>;
}

const EditButton = (props: EditButtonProps): JSX.Element => {
  const { isToggle, setIsToggle, inputValue, contentType, setTextValue } =
    props;
  const handleEditButtonClick = (): void => {
    setIsToggle(true);
  };
  const handleSaveButtonClick = (): void => {
    setIsToggle(false);
    if (contentType === "title") {
      axiosUpdateCabinetTitle(inputValue)
        .then(() => {
          setTextValue(inputValue);
        })
        .catch((error) => {
          console.error(error);
          alert("🚨 수정에 실패했습니다 🚨");
        });
    } else {
      axiosUpdateCabinetMemo(inputValue)
        .then(() => {
          setTextValue(inputValue);
        })
        .catch((error) => {
          console.error(error);
          alert("🚨 수정에 실패했습니다 🚨");
        });
    }
  };

  return isToggle === false ? (
    <Button onClick={handleEditButtonClick}>
      <FontAwesomeIcon icon={faPenToSquare} />
    </Button>
  ) : (
    <Button onClick={handleSaveButtonClick}>
      <FontAwesomeIcon icon={faCircleCheck} />
    </Button>
  );
};

export default EditButton;
