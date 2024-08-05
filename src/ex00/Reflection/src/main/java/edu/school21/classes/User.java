package edu.school21.classes;

import java.util.StringJoiner;

public class User {
    private String firstName;
    private String lastName;
    private Integer height;

    public User() {
        this.firstName = "Default first name";
        this.lastName = "Default last name";
        this.height = 50;
    }

    public User(String firstName) {
        this.firstName = firstName;
        this.lastName = "Wood";
        this.height = 80;
    }

    public int grow(Integer value) {
        this.height += value;
        return height;
    }

    public void walk() {
        System.out.println("Take a walk");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("height=" + height)
                .toString();
    }
}

