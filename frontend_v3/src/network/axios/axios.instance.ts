import axios from "axios";
import { removeCookie } from "../react-cookie/cookie";

const instance = axios.create({
  baseURL: window.location.origin,
});

instance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    // access_token unauthorized
    if (error.response?.status === 401) {
      removeCookie("access_token");
      window.location.href = "/";
    }
    return Promise.reject(error);
  }
);

export default instance;
