package ua.petproject.local_test;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.petproject.annotations.Column;
import ua.petproject.annotations.Entity;
import ua.petproject.annotations.Id;
import ua.petproject.annotations.Table;

@Data
@Entity
@NoArgsConstructor
@Table(name = "person")
public class Person {

    @Id
    @Column(name = "person_id")
    private Integer id;

    @Column(name = "name")
    private String person_name;

    public Person(int i, String s) {
        setId(i);
        setPerson_name(s);
    }
}
