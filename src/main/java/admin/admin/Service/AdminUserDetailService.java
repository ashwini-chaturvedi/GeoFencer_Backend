package admin.admin.Service;

import admin.admin.Entity.Admin;
import admin.admin.Entity.AdminPrincipal;
import admin.admin.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;//for Connectivity with the Database



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin=this.adminRepository.findById(email).orElseThrow();

        if(admin==null){
            System.out.println("Admin Not Found");
            throw new UsernameNotFoundException("Admin Not Found");
        }

        return new AdminPrincipal(admin);
    }
}
