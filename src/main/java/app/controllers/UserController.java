package app.controllers;

import app.dao.UserDAO;
import app.dto.UserDTO;
import app.model.User;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserController implements IUserController {

    private final UserDAO userDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    private UserDTO toDTO(User user) { return new UserDTO(user); }

    @Override
    public Handler getAllUsers() {
        return ctx -> {
            try {
                List<UserDTO> userDTOs = userDAO.getAlleUser()
                        .stream().map(this::toDTO).collect(Collectors.toList());
                ctx.json(userDTOs);
            } catch (Exception e) {
                ctx.status(500).json(Map.of("msg", "Internal server error"));
            }
        };
    }

    @Override
    public Handler createUser() {
        return ctx -> {
            try {
                UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

                // Basic validation
                if (userDTO.getUsername() == null || userDTO.getUsername().isBlank()
                        || userDTO.getEmail() == null || userDTO.getEmail().isBlank()
                        || userDTO.getNewPassword() == null || userDTO.getNewPassword().isBlank()) {
                    ctx.status(400).json(Map.of("msg", "username, email and newPassword are required"));
                    return;
                }

                String hashed = BCrypt.hashpw(userDTO.getNewPassword(), BCrypt.gensalt());
                User created = userDAO.createUser(
                        userDTO.getUsername(),
                        hashed,
                        userDTO.getEmail(),
                        userDTO.getFullName(),
                        userDTO.getPhone()
                );
                ctx.status(HttpStatus.CREATED).json(new UserDTO(created));
            } catch (Exception e) {
                ctx.status(500).json(Map.of("msg", "Internal server error: " + e.getMessage()));
            }
        };
    }

    @Override
    public Handler getUserById() {
        return ctx -> {
            ObjectNode ret = objectMapper.createObjectNode();
            try {
                int userId = Integer.parseInt(ctx.pathParam("id"));
                User user = userDAO.getUserById(userId);
                if (user == null) {
                    ctx.status(404).json(ret.put("msg", "User not found"));
                } else {
                    ctx.json(new UserDTO(user));
                }
            } catch (Exception e) {
                ctx.status(500).json(ret.put("msg", "Internal server error"));
            }
        };
    }

    @Override
    public Handler updateUser() {
        return ctx -> {
            ObjectNode ret = objectMapper.createObjectNode();
            try {
                int userId = Integer.parseInt(ctx.pathParam("id"));
                User existing = userDAO.getUserById(userId);
                if (existing == null) {
                    ctx.status(404).json(ret.put("msg", "User not found"));
                    return;
                }
                UserDTO dto = ctx.bodyAsClass(UserDTO.class);

                if (dto.getUsername() != null) existing.setUsername(dto.getUsername());
                if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
                if (dto.getFullName() != null) existing.setFullName(dto.getFullName());
                if (dto.getPhone() != null) existing.setPhone(dto.getPhone());
                if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
                    existing.setPassword(BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt()));
                }

                userDAO.update(existing);
                ctx.status(HttpStatus.OK).json(new UserDTO(existing));
            } catch (Exception e) {
                ctx.status(500).json(Map.of("msg", "Internal server error: " + e.getMessage()));
            }
        };
    }

    @Override
    public Handler deleteUser() {
        return ctx -> {
            ObjectNode ret = objectMapper.createObjectNode();
            try {
                int userId = Integer.parseInt(ctx.pathParam("id"));
                userDAO.deleteUser(userId);
                ctx.status(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                ctx.status(500).json(ret.put("msg", "Internal server error: " + e.getMessage()));
            }
        };
    }

    @Override
    public Handler getUserByUsername() {
        return ctx -> {
            ObjectNode ret = objectMapper.createObjectNode();
            try {
                String username = ctx.pathParam("username");
                User user = userDAO.findByUsername(username);
                if (user == null) {
                    ctx.status(404).json(ret.put("msg", "User not found"));
                } else {
                    ctx.json(new UserDTO(user));
                }
            } catch (Exception e) {
                ctx.status(500).json(ret.put("msg", "Internal server error"));
            }
        };
    }

    @Override
    public Handler updateUserByUsername() {
        return ctx -> {
            ObjectNode ret = objectMapper.createObjectNode();
            try {
                String username = ctx.pathParam("username");
                User existing = userDAO.findByUsername(username);
                if (existing == null) {
                    ctx.status(404).json(ret.put("msg", "User not found"));
                    return;
                }
                UserDTO dto = ctx.bodyAsClass(UserDTO.class);

                if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
                if (dto.getFullName() != null) existing.setFullName(dto.getFullName());
                if (dto.getPhone() != null) existing.setPhone(dto.getPhone());
                if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
                    existing.setPassword(BCrypt.hashpw(dto.getNewPassword(), BCrypt.gensalt()));
                }

                userDAO.update(existing);
                ctx.status(HttpStatus.OK).json(new UserDTO(existing));
            } catch (Exception e) {
                ctx.status(500).json(ret.put("msg", "Internal server error: " + e.getMessage()));
            }
        };
    }

    @Override
    public Handler logout() {
        return ctx -> {
            ctx.req().getSession().invalidate();
            ctx.redirect("/");
        };
    }
}
