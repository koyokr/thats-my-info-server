package com.rs.privacy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoDTO {
    private String name;
    private int birth;
    private String phoneNumber;
    private String school;
    private String naverId;
}
