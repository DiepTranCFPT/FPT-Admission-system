package com.sba.authentications.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.sba.model.Request.*;
import com.sba.authentications.services.IAuthentication;
import com.sba.model.Response.UserRespone;
import com.sba.model.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
@RestController
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin("*")
@RequestMapping("api/authen")
@Tag(name = "User Controller", description = "Quản lý các hoạt động người dùng như tạo mới, cập nhật, xóa, xác minh, v.v.")
public class AuthenticationController {

    private final IAuthentication authenticationService;

    public AuthenticationController(IAuthentication authenticationService) {
        this.authenticationService = authenticationService;

    }

    @Operation(summary = "Tạo người dùng mới", description = "Đăng ký một người dùng mới với thông tin đã cung cấp.")
    @PostMapping("/register")
    public CompletableFuture<ResponseObject> regisAcount(@RequestBody RegisterRequest registerRequest) throws AccountNotFoundException {
        return authenticationService.register(registerRequest);
    }

    @PostMapping("/login")
    @Operation(summary = "đang nhap (moi quyen)")
    public CompletableFuture<ResponseObject> loginAccount(@RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }


    @PostMapping("/firebase-login")
    @Operation(summary = "dang nhap voi firebase token")
    public CompletableFuture<ResponseObject> firebaseLogin(@RequestBody Map<String, String> request) throws FirebaseAuthException {
        String token = request.get("token");
        System.out.println(token);
        return authenticationService.Oath(token);
    }


    @GetMapping("/profile")
    @Operation(summary = "đang nhap (moi quyen)")
    public String profile(OAuth2AuthenticationToken token, Model model) {
        model.addAttribute("name", token.getPrincipal().getAttribute("name"));
        model.addAttribute("email", token.getPrincipal().getAttribute("email"));
        model.addAttribute("photo", token.getPrincipal().getAttribute("picture"));
        System.out.println("token.getPrincipal().getAttribute()");
        return "user-profile";
    }
    @GetMapping("/signup-with-google")
    @Operation(summary = "đang nhap (moi quyen)")
    public CompletableFuture<ResponseObject> signupWithGoogle(OAuth2AuthenticationToken authenticationToken) throws AccountNotFoundException {
        Map<String, Object> attributes = authenticationToken.getPrincipal().getAttributes();
        LoginGoogleRequest googleLoginRequest = new LoginGoogleRequest();
        googleLoginRequest.setEmail((String) attributes.get("email"));
        googleLoginRequest.setName((String) attributes.get("name"));
        System.out.println(googleLoginRequest);
        return authenticationService.loginByGoogle(googleLoginRequest);
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "lay tat ca nguoi dung co tren he thong (ADMIN)")
    public CompletableFuture<ResponseObject> getAll() {
        return authenticationService.GetAll();
    }

    @PutMapping("/edit")
    @Operation(summary = "sua thong tin tai khoan (ALL ROLE)")
    public String edit(@RequestBody UserRespone userRespone) throws AccountNotFoundException {
        return authenticationService.edit(userRespone);
    }

    @PutMapping("/edit/{id}/{type}")
    @Operation(summary = "thay doi thong tim name & phone (ALL ROLE)")
    public CompletableFuture<ResponseObject> edit(@PathVariable("id") String id, @PathVariable("type") TypeEditUser typeEditUser, String content) {
        return authenticationService.editUserInfor(id, typeEditUser, content);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "khoa va mo khoa tai khoan voi id user hop le (ADMIN)")
    public String delete(@PathVariable("id") String id) throws AccountNotFoundException {
        return authenticationService.delete(id);
    }
    @PostMapping("/forgot-password")
    public void forgotpassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) throws AccountNotFoundException {
        authenticationService.forgotPassword(forgotPasswordRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam("token") String token, @RequestBody ResetPasswordRequest resetPasswordRequest) throws AccountNotFoundException {
        if (authenticationService.resetPassword(resetPasswordRequest) == 1) {
            if (token.equals(resetPasswordRequest.getToken())) {
                return ResponseEntity.ok("Success");
            } else {
                return ResponseEntity.ok("fail");
            }
        }
        return null;
    }
    @PostMapping("/staff")
    @Operation(summary = "Tao tai khoan nhan vien (ADMIN)")
    public CompletableFuture<ResponseObject> createStaff(@RequestParam String id) throws AccountNotFoundException {
        return authenticationService.createStaff(id);
    }
}
