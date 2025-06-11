package admin.admin.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    //Object of the BCryptPasswordEncoder to Encode the Password
    private BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder(12);

    @Autowired
    private AuthenticationManager authManager;//Object of the AuthenticationManager Injected by IOC to verify the user details

    @Autowired
    private JWT_Service jwtService;//Object of the JWT Service so to Generate a JWT token "WE HAVE ADDED DEPENDENCIES"


    public Admin createAdmin(Admin detail) {
        detail.setPassword(passwordEncoder.encode(detail.getPassword()));//Encoding the Password when creating a new Admin
        return adminLibrary.save(detail);
    }

    //Login and Verifying
    //First Use the AuthManager to verify the Admin with UserName/UserEmailId and Password
    //if the Admin is Verified then generate a JWT token and return it...
    public String verifyAdmin(Admin admin) {

        try{
            Authentication authentication=authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(admin.getEmailId(),admin.getPassword()));

            if (authentication.isAuthenticated()) {
                return this.jwtService.generateJWToken(admin.getEmailId());
            } else {
                return "Authentication failed";
            }
        } catch (BadCredentialsException badCredentials) {

            return "Incorrect Email Id or Password! \n Check it and Try Again Later"+badCredentials;
        } catch (UsernameNotFoundException userNotFound) {
            return "⚠️User does not Exist"+userNotFound;
        } catch (AuthenticationException authException) {
            return "Authentication error: " + authException.getMessage(); // fallback for any other auth issues

        }
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

    //Update
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
