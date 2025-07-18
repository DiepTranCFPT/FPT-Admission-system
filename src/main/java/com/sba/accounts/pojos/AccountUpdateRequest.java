package com.sba.accounts.pojos;

import com.sba.enums.Roles;
import lombok.Data;

@Data
public class AccountUpdateRequest {
    private String username;
    private String phoneNumber;
    private String email;
    private Roles role;
    private Boolean enable;
} 