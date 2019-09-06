package com.wirelessiths.dal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String username;
    private String firstName;
    private String lastName;
    private String sub;
    private String phoneNumber;
    private String preferred_username;
    private String cognitoUserStatus;
    private String userStatus;
    private Date userCreateDate;
    private String email;
    private Date lastModifiedDate;

}
