package org.softclicker.server.entity;


/**
 * This class acts as the Clazz entity and intends to use as an immutable object.
 * You can create new {@link Clazz} only by passing a parameters through the constructor.
 */
public class Clazz {
    private int id;
    private String name;
    private int year;

    public Clazz(int id, String name, int year) {
        this.id = id;
        this.name = name;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Clazz{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", year=" + year +
                '}';
    }
}
