package com.android.happ.medicine.data;

/**
 * 회원가입 시 유저의 데이터 형식을 Firebase에 보내는 데이터 클래스
 */
public class UserModel {
    private String email;
    private String password;

    // 기본 생성자
    public UserModel() {
        this.email = "";
        this.password = "";
    }

    // Getter 및 Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}