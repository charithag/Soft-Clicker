package org.softclicker.server.entity;

/**
 * Created by chamika on 5/6/16.
 */
public class Clazz {
    private int id;
    private String name;
    private int year;

    public Clazz(String name, int year) {
        this.name = name;
        this.year = year;
    }

    public Clazz(int id, String name, int year) {
        this.id = id;
        this.name = name;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
