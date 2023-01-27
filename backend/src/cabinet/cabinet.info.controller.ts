import {
  Controller,
  Get,
  Logger,
  Param,
  ParseIntPipe,
  UseGuards,
} from '@nestjs/common';
import {
  ApiBadRequestResponse,
  ApiOkResponse,
  ApiOperation,
  ApiTags,
} from '@nestjs/swagger';
import { JwtAuthGuard } from 'src/auth/jwt/guard/jwtauth.guard';
import { CabinetInfoResponseDto } from 'src/dto/response/cabinet.info.response.dto';
import { CabinetsPerSectionResponseDto } from 'src/dto/response/cabinet.per.section.response.dto';
import { SpaceDataResponseDto } from 'src/dto/response/space.data.response.dto';
import { CabinetInfoService } from './cabinet.info.service';
import { AdminJwtAuthGuard } from 'src/admin/auth/jwt/guard/jwtauth.guard';
import { AdminMainAuthGuard } from 'src/auth/jwt/guard/admin.main.jwtauth.guard';

@ApiTags('Cabinet')
@Controller('api/cabinet_info')
export class CabinetController {
  private logger = new Logger(CabinetController.name);

  constructor(private cabinetService: CabinetInfoService) {}

  @ApiOperation({
    summary: 'space 정보 호출',
    description: 'cabi에 존재하는 건물/층 정보를 받아옵니다.',
  })
  @ApiOkResponse({
    type: SpaceDataResponseDto,
    description:
      'cabi에 존재하는 건물/층 정보를 SpaceDataResponseDto 형식으로 받아옵니다.',
  })
  @Get()
  @UseGuards(AdminMainAuthGuard)
  async getSpaceInfo(): Promise<SpaceDataResponseDto> {
    this.logger.debug(`Called ${this.getSpaceInfo.name}`);
    const cabinetInfo = await this.cabinetService.getSpaceInfo();
    return cabinetInfo;
  }

  @ApiOperation({
    summary: '층 정보 호출',
    description: 'cabi에 존재하는 건물/층 정보를 받아옵니다.',
  })
  @ApiOkResponse({
    type: [CabinetsPerSectionResponseDto],
    description:
      '정상적인 건물 정보와 층 정보가 들어오면 층에 존재하는 section 정보와 사물함 정보를 가져옵니다.',
  })
  @ApiBadRequestResponse({
    description: '비정상 파라미터',
  })
  @Get('/:location/:floor')
  @UseGuards(AdminMainAuthGuard)
  async getCabinetsInfoByParam(
    @Param('location') location: string,
    @Param('floor', ParseIntPipe) floor: number,
  ): Promise<CabinetsPerSectionResponseDto[]> {
    this.logger.debug(`Called ${this.getCabinetsInfoByParam.name}`);
    return await this.cabinetService.getCabinetInfoByParam(location, floor);
  }

  @ApiOperation({
    summary: '사물함 정보 호출',
    description: 'cabinet_id를 받아 특정 사물함의 상세정보를 받아옵니다.',
  })
  @ApiOkResponse({
    type: CabinetInfoResponseDto,
    description:
      '파라미터로 받은 사물함의 정보를 CabinetInfoResponseDto 형식으로 받아옵니다',
  })
  @ApiBadRequestResponse({
    description: '비정상 파라미터',
  })
  @Get('/:cabinet_id')
  @UseGuards(AdminMainAuthGuard)
  async getCabinetInfoById(
    @Param('cabinet_id', ParseIntPipe) cabinetId: number,
  ): Promise<CabinetInfoResponseDto> {
    this.logger.debug(`Called ${this.getCabinetInfoById.name}`);
    return await this.cabinetService.getCabinetResponseInfo(cabinetId);
  }
}
