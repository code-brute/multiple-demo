package com.jackson.spring.demo.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jackson on 2017/7/16.
 */
public class User {
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private Integer age;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
