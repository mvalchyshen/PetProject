package ua.petproject.entity;

import ua.petproject.annotations.Column;
import ua.petproject.annotations.Entity;
import ua.petproject.annotations.Id;

/**
 * Class for testing tables creation.
 */
@Entity
public class Waiter {
    @Id
    @Column
    private Long id;
    @Column
    private String name;
    @Column
    private String phone;
    @Column
    private double revenue;

    public Waiter(){}

    public Waiter(Long id, String name, String phone, double revenue) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.revenue = revenue;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        return "Waiter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", revenue=" + revenue +
                '}';
    }
}
