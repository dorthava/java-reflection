package edu.school21;

@OrmEntity(table = "simple_user")
public class Car {
    @OrmColumnId
    private Long id;
    @OrmColumn(name = "name", length = 10)
    private String name;
    @OrmColumn(name = "color", length = 10)
    private String color;
    @OrmColumn(name ="speed")
    private Double speed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", speed=" + speed +
                '}';
    }
}
