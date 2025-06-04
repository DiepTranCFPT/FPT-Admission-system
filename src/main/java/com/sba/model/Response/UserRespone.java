package com.sba.model.Response;


import com.sba.enums.Roles;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRespone {
    private String id;
    private String name;
    private String email;
    private String phone;
    private Roles role;
    private String plan;
    private boolean enable;
}
