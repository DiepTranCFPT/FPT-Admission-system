package com.sba.model.Response;

import lombok.Builder;

@Builder
public record LoginReponse(String id,
                           String name,
                           String email,
                           String phone,
                           byte[] avata,
                           String token) {
}
