package com.sba.model.Response;

import com.sba.accounts.pojos.Accounts;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NotNull
public class AccountResponse extends Accounts {
    private String token;
}
