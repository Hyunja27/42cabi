import styled from "styled-components";
import SearchListItem from "@/components/TopNav/SearchBar/SearchListItem/SearchListItem";
import { CabinetInfo } from "@/types/dto/cabinet.dto";

interface ISearchListByIntraId {
  intra_id: string;
  user_id: number;
  // cabinet_info: CabinetInfo;
}
/*
cabinet_id: 87
cabinet_num : 7
cabinet_title:null
lent_type: "PRIVATE"
max_user: 1
section: "End of Cluster 1"
status: "SET_EXPIRE_FULL"
lent_info: [{…}]
*/
const SearchList = ({
  searchListById,
  searchListByNum,
  searchWord,
}: {
  searchListById: ISearchListByIntraId[];
  searchListByNum: CabinetInfo[];
  searchWord?: string;
}) => {
  console.log(searchListByNum);
  return (
    <UlStyled>
      {searchListById.map((item, index: number) => {
        return (
          <SearchListItem
            key={index}
            inputText={item.intra_id}
            searchWord={searchWord}
          />
        );
      })}
      {searchListByNum.map((item, index: number) => {
        return (
          <SearchListItem
            key={index}
            inputText={item.cabinet_num.toString()}
            searchWord={searchWord}
            isNum={true}
          />
        );
      })}
    </UlStyled>
  );
};

const UlStyled = styled.ul`
  position: absolute;
  top: 50px;
  left: 0;
  width: 300px;
  border: 1px solid var(--white);
  border-radius: 10px;
  text-align: left;
  padding: 10px;
  color: var(--black);
  background-color: var(--white);
  box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.2);
  opacity: 0.9;
  overflow: hidden;
`;

export default SearchList;
