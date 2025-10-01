package app.dto;

import app.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@Setter
@ToString
public class UserDTO {

    private String username;
    private String password;
    private String email;
    private String fullName;
    private Integer phone;

    private Set<String> roles = new HashSet<>();
    private String newPassword;
    private String oldPassword;

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserDTO(String username, String password, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public UserDTO(String username, String password, String email, String fullName, Integer phone, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName=fullName;
        this.phone = phone;
    }

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.phone = user.getPhone();
        this.roles = user.getRolesAsStrings();

    }

    public UserDTO(String username, Set<String> roleSet) {
        this.username = username;
        this.roles = roleSet;
    }

    public static List<UserDTO> toUserDTOList(List<User> users) {
        List<UserDTO> userDTOList = new ArrayList<>();
        for (User user : users) {
            userDTOList.add(new UserDTO(user));
        }
        return userDTOList;
    }


}