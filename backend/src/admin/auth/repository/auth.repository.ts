import { InjectRepository } from '@nestjs/typeorm';
import { AdminUserDto } from 'src/admin/dto/admin.user.dto';
import AdminUserRole from 'src/admin/enums/admin.user.role.enum';
import AdminUser from 'src/entities/admin.user.entity';
import { Repository } from 'typeorm';
import { IAdminAuthRepository } from './auth.repository.interface';

export class AdminAuthRepository implements IAdminAuthRepository {
  constructor(
    @InjectRepository(AdminUser)
    private adminUserRepository: Repository<AdminUser>,
  ) {}

  async addUserIfNotExists(adminUser: AdminUserDto): Promise<boolean> {
    const find = await this.adminUserRepository.findOne({
      where: {
        email: adminUser.email,
      },
    });
    if (!find) {
      await this.adminUserRepository.save({
        email: adminUser.email,
        role: adminUser.role,
      });
      return false;
    }
    return true;
  }

  async checkUserExists(email: string): Promise<boolean> {
    const result = await this.adminUserRepository
      .createQueryBuilder('au')
      .select(['au.email'])
      .where('au.email = :email', { email })
      .execute();
    return result.length !== 0;
  }

  async getAdminUserRole(email: string): Promise<AdminUserRole> {
    const result = await this.adminUserRepository.findOne({
      select: {
        role: true,
      },
      where: {
        email,
      },
    });
    return result ? result.role : AdminUserRole.AUTHORIZE_WAITING;
  }
}
