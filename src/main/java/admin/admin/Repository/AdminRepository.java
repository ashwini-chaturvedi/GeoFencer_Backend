package admin.admin.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import admin.admin.Entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String>, CrudRepository<Admin,String> {


    boolean existsByEmailId(String email);

    List<Admin> findAllByUserName(String name);

    void deleteByEmailId(String email);
}
