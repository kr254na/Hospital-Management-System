package repository;

import enums.Gender;
import enums.Role;
import exceptions.InvalidInputException;
import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class UserJdbcRepository {

    public static void save(User user, Connection conn) throws SQLException{
        String sql = "INSERT INTO users (user_id, username, password_hash, role, " +
                "is_active, full_name, contact_info, gender, date_of_birth) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println(user.getContact());
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getId().toString());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getRole().name());
            pstmt.setBoolean(5, user.isActive());
            pstmt.setString(6, user.getName());
            pstmt.setString(7, user.getContact());
            pstmt.setString(8, user.getGender().name());
            pstmt.setDate(9, Date.valueOf(user.getDob()));
            pstmt.executeUpdate();
            Logger.getLogger(UserJdbcRepository.class.getName())
                    .info("User saved to database successfully");

        }
    }

    public Optional<User> findByUsername(String username, Connection conn) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findById(UUID id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    public void updateActiveStatus(UUID userId, boolean status, Connection conn) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, status);
            pstmt.setString(2, userId.toString());
            pstmt.executeUpdate();
        }
    }

    public void updatePassword(UUID userId, String newPasswordHash, Connection conn) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPasswordHash);
            pstmt.setString(2, userId.toString());
            pstmt.executeUpdate();
        }
    }

    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        try{
            User user = new User(
                    rs.getString("username"),
                    rs.getString("password_hash"),
                    Role.valueOf(rs.getString("role")),
                    rs.getString("full_name"),
                    rs.getString("contact_info"),
                    Gender.valueOf(rs.getString("gender")),
                    rs.getDate("date_of_birth").toLocalDate()
            );
            user.setUserId(UUID.fromString(rs.getString("user_id")));
            return user;
        }
        catch (Exception e){
            throw new SQLException("Error mapping test report data: " + e.getMessage());
        }
    }

    public void updatePersonalDetails(UUID userId, String name, String contact, Connection conn) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, contact_info = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, userId.toString());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating personal details failed, no user found with ID: " + userId);
            }
        }
    }

    public List<User> findByName(String name, Connection conn) throws SQLException, InvalidInputException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE LOWER(full_name) LIKE LOWER(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            Role.valueOf(rs.getString("role")),
                            rs.getString("full_name"),
                            rs.getString("contact_info"),
                            enums.Gender.valueOf(rs.getString("gender")),
                            rs.getDate("date_of_birth").toLocalDate()
                    ));
                }
            }
        }
        return users;
    }
}