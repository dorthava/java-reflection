package edu.school21;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Class<?> userClass = User.class;
        User newUser = new User();

        newUser.setAge(14);
        newUser.setFirstName(null);
        newUser.setLastName("Potter");

        OrmManager ormManager = new OrmManager("jdbc:postgresql://localhost:5432/chat");
        try {
            ormManager.initialize(userClass);
            ormManager.save(newUser);
            User findUser = (User) ormManager.findById(1L, userClass);
            findUser.setAge(44);
            ormManager.update(findUser);
        } catch (SQLException | IllegalAccessException e) {
            System.err.println(e.getMessage());
        }
    }
}
