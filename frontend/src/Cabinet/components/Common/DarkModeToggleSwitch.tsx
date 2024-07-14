import React from "react";
import styled from "styled-components";
import { ReactComponent as MoonIcon } from "@/Cabinet/assets/images/moonAdmin.svg";
import { ReactComponent as SunIcon } from "@/Cabinet/assets/images/sunAdmin.svg";

interface ToggleSwitchInterface {
  id: string;
  onChange?: (checked: boolean) => void;
  checked: boolean;
  disabled?: boolean;
}

const DarkModeToggleSwitch = ({
  id,
  onChange,
  checked = false,
  disabled,
}: ToggleSwitchInterface) => {
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newState = event.target.checked;
    if (onChange) onChange(newState);
  };

  return (
    <ToggleSwitchContainerStyled disabled={disabled}>
      <InputStyled
        type="checkbox"
        id={id}
        checked={checked}
        onChange={handleChange}
        disabled={disabled}
      />
      <ToggleSwitchStyled htmlFor={id} checked={checked} disabled={disabled}>
        <SunWrapperStyled>
          <SunIcon />
        </SunWrapperStyled>
        <MoonWrapperStyled>
          <MoonIcon />
        </MoonWrapperStyled>
        <ToggleKnobStyled checked={checked} />
      </ToggleSwitchStyled>
    </ToggleSwitchContainerStyled>
  );
};

const ToggleSwitchContainerStyled = styled.div<{ disabled?: boolean }>`
  display: inline-block;
  position: relative;
  margin-right: 10px;
  opacity: ${(props) => (props.disabled ? 0.5 : 1)};
`;

const InputStyled = styled.input.attrs({ type: "checkbox" })`
  opacity: 0;
  position: absolute;
  width: 0;
  height: 0;
`;

const ToggleSwitchStyled = styled.label<{
  checked: boolean;
  disabled?: boolean;
}>`
  cursor: ${(props) => (props.disabled ? "not-allowed" : "pointer")};
  display: inline-block;
  position: relative;
  background: ${(props) =>
    props.checked ? "var(--sys-main-color)" : "var(--line-color)"};
  width: 46px;
  height: 24px;
  border-radius: 50px;
  transition: background-color 0.2s ease;
`;

const ToggleKnobStyled = styled.span<{ checked: boolean }>`
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  left: ${(props) => (props.checked ? "calc(100% - 21px)" : "3px")};
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--white-text-with-bg-color);
  transition: left 0.2s;
`;

const SunWrapperStyled = styled.div`
  position: absolute;
  right: 5px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;

  & > svg {
    width: 100%;
    height: 100%;
  }
`;

const MoonWrapperStyled = styled.div`
  position: absolute;
  left: 5px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;

  & > svg {
    width: 100%;
    height: 100%;
    fill: var(--black-text-color);
  }
`;

export default DarkModeToggleSwitch;
