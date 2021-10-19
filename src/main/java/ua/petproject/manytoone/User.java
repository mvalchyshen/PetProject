package ua.petproject.manytoone;

import lombok.Data;
import ua.petproject.annotations.Column;
import ua.petproject.annotations.Id;
import ua.petproject.annotations.Table;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
@Data
@Table
public class User {
    @Id
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;
}
