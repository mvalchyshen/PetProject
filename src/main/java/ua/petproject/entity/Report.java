package ua.petproject.entity;

import ua.petproject.annotations.*;

@Entity
@Table(name = "report")
public class Report {
    @Id
    @AutoGeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;


    public Report(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Report(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }
}