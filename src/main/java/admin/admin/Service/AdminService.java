package admin.admin.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import admin.admin.Entity.Admin;
import admin.admin.Library.AdminLibrary;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AdminService {

    @Autowired
    private AdminLibrary adminLibrary;

    private BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder(12);

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWT_Service jwtService;

    public Admin createAdmin(Admin detail) {
        detail.setPassword(passwordEncoder.encode(detail.getPassword()));//Encoding the Password when creating a new Admin
        return adminLibrary.save(detail);
    }

    public Admin save(Admin details){
        return this.adminLibrary.save(details);
    }

    //Login and Verifying
    public String verifyAdmin(Admin admin) {
        Authentication authentication=authManager
                .authenticate(new UsernamePasswordAuthenticationToken(admin.getEmailId(),admin.getPassword()));

       if(authentication.isAuthenticated()) {
           return this.jwtService.generateJWToken(admin.getEmailId());
       }
       return "Fail";
    }

    public Admin findByEmailId(String email) {
        return adminLibrary.findByEmailId(email);
    }

    public boolean exists(String email) {
        return adminLibrary.exists(email);
    }

    public List<Admin> findByUserName(String name) {
        return adminLibrary.findByUserName(name);
    }

    public Admin updateAdmin(String email, Admin admin) {
        Admin existingAdmin = findByEmailId(email);
        existingAdmin.setUserName(admin.getUserName());
        existingAdmin.setPhoneNo(admin.getPhoneNo());
        existingAdmin.setFullName(admin.getFullName());

        return adminLibrary.save(existingAdmin);
    }

    public Admin updatePassword(String email,String newPassword){
        Admin existingAdmin = findByEmailId(email);
        existingAdmin.setPassword(this.passwordEncoder.encode(newPassword));

        return this.adminLibrary.save(existingAdmin);
    }

    public void delete(Admin admin) {
        adminLibrary.delete(admin);
    }

    public void deleteByEmail(String email) {
        adminLibrary.deleteByEmail(email);
    }


}
