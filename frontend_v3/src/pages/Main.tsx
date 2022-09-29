import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import CabinetTemplate from "../components/templates/CabinetTemplate";
import FooterTemplate from "../components/templates/FooterTemplate";
import ContentTemplate from "../components/templates/ContentTemplate";
import { useAppDispatch, useAppSelector } from "../redux/hooks";
import { axiosMyInfo } from "../network/axios/axios.custom";
import { userAll } from "../redux/slices/userSlice";

const Main = (): JSX.Element => {
  const user = useAppSelector((state) => state.user);
  const navigate = useNavigate();
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (user.intra_id === "default") {
      axiosMyInfo()
        .then((response) => {
          dispatch(userAll(response.data));
        })
        .catch((error) => {
          navigate("/");
        });
    }
  }, []);

  return (
    <>
      <ContentTemplate>
        <CabinetTemplate />
      </ContentTemplate>
      <FooterTemplate />
    </>
  );
};

export default Main;
