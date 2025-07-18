package com.sba.model.Response;

import com.sba.enums.Roles;
import lombok.Builder;

@Builder
public record LoginReponse(String id,
                           String name,
                           String email,
                           String role,
                           String phone,
                           byte[] avata,
                           String token) {
}
