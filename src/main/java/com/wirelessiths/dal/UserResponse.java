package com.wirelessiths.dal;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
public class UserResponse {

    private String username;
    private String userStatus;
    private Date userCreateDate;
    private String email;
    private Date lastModifiedDate;

}
