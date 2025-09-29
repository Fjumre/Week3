package app.model;


import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashSet;
import java.util.Set;


@Getter
@ToString
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id", nullable = false, unique = true)
    private int id;
    
    @Column(name = "username", nullable = false, unique= true, length = 40)
    private String username;

    @Column(name = "password", nullable = false, length = 50)
    private String  password;
    
    @Column(name= "email", nullable = false, unique= true)
    private String email;
    
    @Column(name= "fullName", nullable= false)
    private String fullName;
    
    @Column(name= "phone", nullable= false)
    private int phone;
    
    @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
                name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
                inverseJoinColumns = @JoinColumn(name = "roleName", referencedColumnName = "roleName")
        )
        private Set<Role> roles = new HashSet<>();
        
        
         public User(String username, String password, Set<Role> roles) {
                this.username = username;
                this.password = BCrypt.hashpw(password, BCrypt.gensalt());
                this.roles = roles;
            }
        
            public User(String username, String password, String email, String fullName, int phone) {
                this.username = username;
                this.password = BCrypt.hashpw(password, BCrypt.gensalt());
                this.email = email;
                this.fullName= fullName;
                this.phone = phone;
            }
        
            public User(String password) {
                this.password = BCrypt.hashpw(password, BCrypt.gensalt());
            }
          public void setPassword(String password) {
                this.password = BCrypt.hashpw(password, BCrypt.gensalt());
            }
            
            public boolean verifyUser(String password) {
            return BCrypt.checkpw(password, this.password);
    }
    
    public void addRole(Role role) {
            roles.add(role);
            role.getUsers().add(this);
        }
    
        public void removeRole(Role role) {
            roles.remove(role);
            role.getUsers().remove(this);
        }
        
        public Set<String> getRolesAsStrings() {
                if (roles.isEmpty()) {
                    return null;
                }
                Set<String> rolesAsStrings = new HashSet<>();
                roles.forEach(role -> rolesAsStrings.add(role.getRolename()));
                return rolesAsStrings;
            }
}