package admin.admin.Repository;

import admin.admin.Entity.OTP;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OTPRepository extends CrudRepository<OTP,String> {

}
