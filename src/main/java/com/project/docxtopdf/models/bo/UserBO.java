package com.project.docxtopdf.models.bo;

import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.models.dao.UserDAO;

public class UserBO {

    public static User checkLogin(String username, String password) {
        return UserDAO.checkCredentials(username, password);
    }

    public static boolean addUser(String username, String password) {
        return UserDAO.registerUser(username, password);
    }
}
