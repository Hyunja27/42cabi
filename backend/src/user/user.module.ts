import { forwardRef, Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AdminLogModule } from 'src/admin/log/log.module';
import { AdminLogService } from 'src/admin/log/log.service';
import { AuthModule } from 'src/auth/auth.module';
import User from 'src/entities/user.entity';
import { BanModule } from '../ban/ban.module';
import { CabinetModule } from '../cabinet/cabinet.module';
import { MyInfoController } from './my.info.controller';
import { MyLentInfoController } from './my.lent.info.controller';
import { UserRepository } from './repository/user.repository';
import { UserService } from './user.service';

const repo = {
  provide: 'IUserRepository',
  useClass: UserRepository,
};

@Module({
  imports: [
    AuthModule,
    forwardRef(() => BanModule),
    CabinetModule,
    TypeOrmModule.forFeature([User]),
    AdminLogModule,
  ],
  controllers: [MyLentInfoController, MyInfoController],
  providers: [UserService, repo, AdminLogService],
  exports: [UserService],
})
export class UserModule {}
