package com.zerobase.ims.user.entity.dto;


import com.zerobase.ims.common.type.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {
    private String email;
    private String nickname;
    private String password;
    private Role role;

}
