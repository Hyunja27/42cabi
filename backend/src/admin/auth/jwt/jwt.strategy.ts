import { ExtractJwt, Strategy } from 'passport-jwt';
import { PassportStrategy } from '@nestjs/passport';
import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { AdminAuthService } from 'src/admin/auth/auth.service';
import AdminUserRole from 'src/admin/enums/admin.user.role.enum';
@Injectable()
export class AdminJwtStrategy extends PassportStrategy(Strategy, 'admin_jwt') {
  constructor(
    private configService: ConfigService,
    private adminAuthService: AdminAuthService,
  ) {
    super({
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
      secretOrKey: configService.get<string>('jwt.secret'),
    });
  }

  async validate(payload: any) {
    const exist = await this.adminAuthService.checkUserExists(payload.email);
    const adminRole = payload.role;
    if (!payload || (!exist && adminRole != AdminUserRole.ROOT_ADMIN)) {
      return false;
    }
    // 검증이 필요없다 판단함
    return payload;
  }
}
