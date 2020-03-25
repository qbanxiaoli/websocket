package com.entity;

import lombok.Data;

import java.security.Principal;
import java.util.List;

/**
 * @author qbanxiaoli
 * @description
 * @create 2020-03-25 16:05
 */
@Data
public class User implements Principal {

    private String username;

    private String id;

    private String role;

    private List<String> urls;

    @Override
    public String getName() {
        return username;
    }
}
