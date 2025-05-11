package admin.admin.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import admin.admin.Entity.Admin;
import admin.admin.Entity.Device;
import admin.admin.Service.AdminService;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService service;

    @PostMapping
    public ResponseEntity<Admin> addAdmin(@RequestBody Admin admin) {
        try {
            Admin createdAdmin = service.createAdmin(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody Admin admin){
//        System.out.println("Admin:"+admin);
//        try {
//            String msg= this.service.verifyAdmin(admin);
//
//            return ResponseEntity.ok(msg);
//        } catch (Exception e) {
//
//            return ResponseEntity.ok(e.getLocalizedMessage());
//        }
//    }

    @GetMapping("/{email}")
    public ResponseEntity<Admin> getAdminByEmail(@PathVariable String email) {
        try {
            Admin admin = service.findByEmailId(email);

            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/username/{userName}")
    public ResponseEntity<List<Admin>> getAdminsByUserName(@PathVariable String userName) {
        try {
            List<Admin> admins = service.findByUserName(userName);
            return admins.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).build() : ResponseEntity.ok(admins);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{email}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable String email, @RequestBody Device device) {
        try {
            Admin updatedAdmin = service.updateAdmin(email, device);
            return ResponseEntity.ok(updatedAdmin);
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteAdminByEmail(@PathVariable String email) {
        try {
            service.deleteByEmail(email);
            return ResponseEntity.ok("Admin with email " + email + " deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
