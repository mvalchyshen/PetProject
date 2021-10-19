package ua.petproject.manytoone;

import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;
@Data
public class Test {

    @OneToMany(mappedBy = "test", fetch = FetchType.EAGER)
    private Set<User> userSet;


}
