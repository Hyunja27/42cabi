import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AdminAuthModule } from 'src/admin/auth/auth.module';
import { AdminDevelopController } from 'src/admin/develop/develop.controller';
import { AdminDevelopService } from 'src/admin/develop/develop.service';
import { AdminDevelopRepository } from 'src/admin/develop/repository/develop.repository';
import AdminUser from 'src/entities/admin.user.entity';

const adminDevelopRepo = {
  provide: 'IAdminDevelopRepository',
  useClass: AdminDevelopRepository,
};

@Module({
  controllers: [AdminDevelopController],
  providers: [adminDevelopRepo, AdminDevelopService],
  imports: [TypeOrmModule.forFeature([AdminUser]), AdminAuthModule],
  exports: [],
})
export class AdminDevelopModule {}
