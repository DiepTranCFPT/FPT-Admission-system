package com.sba.accounts.controller;

import com.sba.accounts.pojos.AccountUpdateRequest;
import com.sba.accounts.pojos.Accounts;
import com.sba.accounts.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin("*")
public class AccountController {
    @Autowired
    private AccountService accountService;

    // User tự cập nhật thông tin của mình
    @PutMapping("/update")
    public ResponseEntity<Accounts> updateSelf(@RequestBody AccountUpdateRequest req) {
        Accounts updated = accountService.updateAccount(null, req);
        return ResponseEntity.ok(updated);
    }

    // Admin cập nhật thông tin user bất kỳ
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Accounts> updateById(@PathVariable String id, @RequestBody AccountUpdateRequest req) {
        Accounts updated = accountService.updateAccount(id, req);
        return ResponseEntity.ok(updated);
    }

    // Lấy thông tin user hiện tại
    @GetMapping("/me")
    public ResponseEntity<Accounts> getCurrentUser() {
        Accounts acc = accountService.getCurrentUser();
        return ResponseEntity.ok(acc);
    }

    // Lấy thông tin user theo id (chỉ admin)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Accounts> getById(@PathVariable String id) {
        Accounts acc = accountService.getById(id);
        return ResponseEntity.ok(acc);
    }

    // Lấy toàn bộ user (chỉ admin)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(accountService.getAll());
    }






















































































    // xoa user

} 