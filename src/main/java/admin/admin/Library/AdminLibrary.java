package admin.admin.Library;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import admin.admin.Entity.Admin;
import admin.admin.Repository.AdminRepository;

@Component
public class AdminLibrary {

    @Autowired
    private AdminRepository repo;

    //Create
    public Admin save(Admin detail) {
        return repo.save(detail);
    }

    //Read
    public Admin findByEmailId(String email) {
        return repo.findById(email).orElseThrow();
    }

    public boolean exists(String email) {
        return repo.existsByEmailId(email);
    }

    public List<Admin> findByUserName(String name) {
        return repo.findAllByUserName(name);
    }


    //Delete
    public void delete(Admin admin) {
        repo.delete(admin);
    }

    public void deleteByEmail(String email) {
        repo.deleteByEmailId(email);
    }
}
