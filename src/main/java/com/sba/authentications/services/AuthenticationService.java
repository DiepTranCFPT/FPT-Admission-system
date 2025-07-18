package com.sba.authentications.services;


import com.sba.accounts.pojos.Accounts;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.sba.enums.Roles;
import com.sba.exceptions.GlobalException;
import com.sba.model.Request.*;
import com.sba.exceptions.AuthException;
import com.sba.model.EmailDetail;
import com.sba.model.Response.AccountResponse;
import com.sba.model.Response.LoginReponse;
import com.sba.model.Response.UserRespone;
import com.sba.model.ResponseObject;
import com.sba.authentications.repositories.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;
import java.util.concurrent.CompletionException;


@Service
public class AuthenticationService implements IAuthentication {


    private final AuthenticationRepository authenticationRepository;

    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;


    @Autowired
    public AuthenticationService(AuthenticationRepository authenticationRepository,
                                 TokenService tokenService,
                                 PasswordEncoder passwordEncoder,
                                 EmailService emailService
//            ,AccountUtils accountUtils

    ) {
        this.authenticationRepository = authenticationRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    @Async
    @Transactional
    public CompletableFuture<ResponseObject> login(LoginRequest loginRequest) {

        Accounts accounts = authenticationRepository.findByEmail(loginRequest.getEmail())
                .map(user1 -> {
                    if (user1.isDeleted()) {
                        throw new UsernameNotFoundException("Account has been deleted!");
                    }
                    if (!user1.isEnable()) {
                        throw new UsernameNotFoundException("Account is not enabled!");
                    }
                    if (!passwordEncoder.matches(loginRequest.getPassword(), user1.getPassword()))
                        throw new UsernameNotFoundException("Incorrect password!");
                    return user1;
                })
                .orElseThrow(() -> new UsernameNotFoundException("Account is not exists!"));


        return CompletableFuture.supplyAsync(() -> {

            LoginReponse accountResponse = LoginReponse.builder()
                    .name(accounts.getUsername())
                    .token(tokenService.generateToken(accounts))
                    .phone(accounts.getPhoneNumber() == null ? "" : accounts.getPhoneNumber())
                    .email(accounts.getEmail())
                    .id(accounts.getId())
                    .role(accounts.getRole().toString())
                    .build();
            return ResponseObject.builder()
                    .data(accountResponse)
                    .message("Login successful")
                    .httpStatus(HttpStatus.OK)
                    .build();
        });
    }

    @Transactional
    @Override
    @Async
    public CompletableFuture<ResponseObject> register(RegisterRequest registerRequest) throws AccountNotFoundException {

        boolean check = authenticationRepository.existsByEmail(registerRequest.getEmail());
        System.out.println(check);
        if (check) {
            throw new AccountNotFoundException("email exists!");
        }
        return CompletableFuture.supplyAsync(() -> {
            Accounts accounts = Accounts.builder()
                    .username(registerRequest.getName())
                    .email(registerRequest.getEmail() == null ? "" : registerRequest.getEmail())
                    .role(Roles.USER)
//                    .phone(registerRequest.getPhone() == null ? "" : registerRequest.getPhone())
                    .enable(true)
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .deleted(false).build();
            authenticationRepository.saveAndFlush(accounts);
            return ResponseObject.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("register successfully!")
                    .data(true)
                    .build();
        });
    }

    @Override
    public CompletableFuture<ResponseObject> Oath(String token) throws FirebaseAuthException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String email = firebaseToken.getEmail();
                System.out.println(email);

                // Tìm user theo email
                Accounts accountsOpt = authenticationRepository.findByEmail(email).map(user -> {
                    if (user.isDeleted()) {
                        throw new RuntimeException("account deleted");
                    }
                    if (!user.isEnable()) {
                        throw new UsernameNotFoundException("Account is not enabled!");
                    }
                    return user;
                }).orElse(null);

                if (accountsOpt != null) {

                    return ResponseObject.builder()
                            .httpStatus(HttpStatus.OK)
                            .data(LoginReponse.builder()
                                    .email(accountsOpt.getEmail())
                                    .name(accountsOpt.getUsername())
                                    .phone(accountsOpt.getPhoneNumber())
                                    .id(accountsOpt.getId())
                                    .token(tokenService.generateToken(accountsOpt))
                                    .build())
                            .build();
                }

                // Nếu user không tồn tại, tạo user mới
                Accounts newAccounts = Accounts.builder()
                        .firebaseUid(firebaseToken.getUid())
                        .email(email)
                        .username(firebaseToken.getName())
                        .role(Roles.USER)
                        .enable(true).deleted(false).build();
                authenticationRepository.saveAndFlush(newAccounts);
                return ResponseObject.builder()
                        .httpStatus(HttpStatus.OK)
                        .data(LoginReponse.builder()
                                .email(newAccounts.getEmail())
                                .name(newAccounts.getUsername())
                                .phone(newAccounts.getPhoneNumber())
                                .id(newAccounts.getId())
                                .token(tokenService.generateToken(newAccounts))
                                .build())
                        .build();

            } catch (FirebaseAuthException e) {
                throw new CompletionException(e);
            }
        });
    }

    @Override
    @Transactional
    public CompletableFuture<ResponseObject> GetAll() {
        List<Accounts> users = authenticationRepository.findAll();
        List<UserRespone> accountResponses = new ArrayList<>();

        for (Accounts user : users) {
            String planName = "No Plan";

            accountResponses.add(UserRespone.builder()
                    .email(user.getEmail())
                    .name(user.getUsername())
                    .role(user.getRole())
                    .phone(user.getPhoneNumber())
                    .id(user.getId())
                    .enable(user.isEnable())
                    .plan(planName)
                    .build());
        }

        return CompletableFuture.supplyAsync(() -> ResponseObject.builder()
                .httpStatus(HttpStatus.OK)
                .data(accountResponses)
                .message("Get all successfully!")
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String edit(UserRespone userRespone) throws AccountNotFoundException {
        Accounts accounts = authenticationRepository.findById(userRespone.getId()).orElseThrow(() -> new AccountNotFoundException("Account does not exist"));
        if(accounts.isDeleted()){
            throw new AccountNotFoundException("account deleted");
        }
        accounts.setUsername(userRespone.getName());
        accounts.setEmail(userRespone.getEmail());
        accounts.setPhoneNumber(userRespone.getPhone());
        accounts.setEnable(userRespone.isEnable());
        accounts.setRole(userRespone.getRole());
        authenticationRepository.saveAndFlush(accounts);
        return "Success";
    }

    @Override
    public String delete(String id) throws AccountNotFoundException {
        Accounts accounts = authenticationRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("Account does not exist"));
        ;
        accounts.setEnable(!accounts.isEnable());
        authenticationRepository.saveAndFlush(accounts);
        return "Success";
    }

    @Override
    @Transactional
    public CompletableFuture<ResponseObject> editUserInfor(String id, TypeEditUser typeEditUser, String content) {

        Accounts accounts = authenticationRepository.findById(id).orElseThrow(() -> new RuntimeException("Account does not exist"));
        if (!accounts.isEnable()) {
            throw new UsernameNotFoundException("Account is not enabled!");
        }
        if (accounts.isDeleted()) {  // Kiểm tra đúng logic
            throw new UsernameNotFoundException("Account is deleted!");
        }

        switch (typeEditUser) {
            case Name -> accounts.setUsername(content);
            case Phone -> {
                if (!content.matches("\\d{10,15}")) {
                    throw new IllegalArgumentException("Phone number must be between 10 and 15 digits and contain only numbers.");
                }
                accounts.setPhoneNumber(content);
            }
            default -> throw new UsernameNotFoundException("Invalid type edit!");
        }
        authenticationRepository.saveAndFlush(accounts);
        return CompletableFuture.completedFuture(ResponseObject.builder()
                .message("edit successfully!")
                .data(true)
                .httpStatus(HttpStatus.OK)
                .build());
    }

    @Transactional
    @Override
    @Async
    public CompletableFuture<ResponseObject> loginByGoogle(LoginGoogleRequest loginGoogleRequest) throws AccountNotFoundException {
        // Tìm kiếm tài khoản bằng email
        Accounts account = authenticationRepository.findByEmail(loginGoogleRequest.getEmail())
                .orElseThrow(() -> new AccountNotFoundException("Account does not exist"));
        // Nếu tài khoản không tồn tại, tạo tài khoản mới
        if (account == null) {
            // Tạo tài khoản mới
            account = Accounts.builder()
                    .username(loginGoogleRequest.getName())
                    .email(loginGoogleRequest.getEmail())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Sử dụng một password tạm thời
                    .role(Roles.USER)
                    .enable(true)
                    .verificationCode(UUID.randomUUID().toString())
                    .deleted(false).build();

            // Lưu tài khoản vào cơ sở dữ liệu
            try {
                authenticationRepository.saveAndFlush(account);
            } catch (DataIntegrityViolationException e) {
                throw new AuthException("Duplicate account creation error.");
            }
        }

        if (!account.isEnable()) {
            throw new AuthException("Account not verified. Please check your email to verify your account.");
        }
        String token = tokenService.generateToken(account);

        // Trả về thông tin tài khoản đã đăng nhập
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setId(account.getId());
        accountResponse.setEmail(account.getEmail());
        accountResponse.setToken(token);
        accountResponse.setUsername(account.getUsername());
        accountResponse.setPhoneNumber(account.getPhoneNumber());

        // Trả về thông tin phản hồi
        return CompletableFuture.supplyAsync(() -> {
            return ResponseObject.builder()
                    .data(accountResponse)
                    .message("Login successful")
                    .httpStatus(HttpStatus.OK)
                    .build();
        });
    }
    @Override
    public int resetPassword(ResetPasswordRequest resetPasswordRequest) throws AccountNotFoundException {
        Accounts accounts = authenticationRepository.findByEmail(resetPasswordRequest.getEmail()).orElseThrow(()-> new AccountNotFoundException("Account not found"));
        String token = tokenService.generateToken(accounts);
        // Check if the token matches
        if (!token.equals(resetPasswordRequest.getToken())) {
            throw new GlobalException("Invalid token");
        }else {
            accounts.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            authenticationRepository.save(accounts);
            return 1;
        }

    }

    @Override
    public CompletableFuture<ResponseObject> createStaff(String id) throws AccountNotFoundException {
        Accounts account = authenticationRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("Account does not exist"));
        account.setRole(Roles.STAFF);
        authenticationRepository.save(account);
        return CompletableFuture.supplyAsync(() -> ResponseObject.builder()
                .data(account)
                .message("create successful")
                .httpStatus(HttpStatus.OK)
                .build());
    }
    @Override
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) throws AccountNotFoundException {
        Accounts account = authenticationRepository.findByEmail(forgotPasswordRequest.getEmail()).orElseThrow(()-> new AccountNotFoundException("Account not found"));
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(forgotPasswordRequest.getEmail());
        emailDetail.setSubject("Reset Password for account " + forgotPasswordRequest.getEmail() + "!!!");
        emailDetail.setMsgBody(""); // You might want to add a meaningful message here
        emailDetail.setButtonValue("Reset Password");
        emailDetail.setLink("https://fpt-admission-system.onrender.com/api/authen/reset-password?token=" + tokenService.generateToken(account));
        emailDetail.setName(account.getUsername());

        Runnable r = new Runnable() {
            @Override
            public void run() {
                emailService.sendMailTemplateForgot(emailDetail);
            }
        };

        new Thread(r).start();
    }



}
