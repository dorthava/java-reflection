package edu.school21.classes;

import java.util.StringJoiner;

public class Car {
    private String model;
    private Integer price;
    private Integer year;

    public Car() {
        this.model = "Default model";
        this.price = 100;
        this.year = 2024;
    }

    public Car(String model) {
        this.model = model;
        this.price = 200;
        this.year = 1995;
    }

    public void drive() {
        System.out.println("The car is driving");
    }

    public Integer addYear(Integer count) {
        this.year += count;
        return this.year;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Car.class.getSimpleName() + "[", "]")
                .add("model='" + model + "'")
                .add("price=" + price)
                .add("year=" + year)
                .toString();
    }
}
