package services;

import exceptions.InvalidInputException;
import models.User;
import repository.UserJdbcRepository;
import utility.PasswordUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserJdbcRepository userRepo = new UserJdbcRepository();

    public User authenticate(String username, String password, Connection conn) throws SQLException {
        if (username == null || password == null || username.isBlank()) {
            return null;
        }
        Optional<User> userOpt = userRepo.findByUsername(username, conn);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPasswordHash().equals(PasswordUtil.hashPassword(password))) {
                return user;
            }
        }
        return null;
    }

    public void updatePersonalDetails(UUID userId, String name, String contact, Connection conn) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        userRepo.updatePersonalDetails(userId, name, contact, conn);
    }

    public void updatePassword(UUID userId, String newPassword, Connection conn) throws SQLException {
        userRepo.updatePassword(userId, newPassword, conn);
    }

    public void updateActiveStatus(UUID userId, boolean status, Connection conn) throws SQLException {
        userRepo.updateActiveStatus(userId, status, conn);
    }

    public List<User> findByName(String name, Connection conn) throws SQLException, InvalidInputException {
        return userRepo.findByName(name, conn);
    }
}