package com.sba.model.Request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String token;
    private String newPassword;
}
