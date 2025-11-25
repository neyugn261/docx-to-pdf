package com.project.docxtopdf.models.bo;

import com.project.docxtopdf.models.bean.User;
import com.project.docxtopdf.models.dao.UserDAO;

public class UserBO {

    public static User login(String username, String password) {
        return UserDAO.login(username, password);
    }

    public static boolean register(String username, String password) {
        return UserDAO.register(username, password);
    }
}
