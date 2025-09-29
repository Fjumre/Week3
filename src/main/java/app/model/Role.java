package app.model;

import app.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role{

    @Id
    @Column(name= "roleName", nullable= false, unique= true)
    private String roleName;
    
    @Setter
    @Getter
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<User> users = new HashSet<>();
    
    public Role(String roleName) {
            this.roleName = roleName;
    }

    public String getRolename() {
        return this.roleName;
    }
}