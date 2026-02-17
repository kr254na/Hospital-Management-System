package sql;

import config.DbConfig;
import exceptions.InvalidInputException;
import models.User;
import enums.Role;
import enums.Gender;
import repository.UserJdbcRepository;
import utility.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class DatabaseSeeder {

    public static void seedAdmin() {
        System.out.println("Checking system for administrative accounts...");
        try (Connection conn = DbConfig.getConnection()) {
            UserJdbcRepository repo = new UserJdbcRepository();
            if (repo.findByUsername("admin", conn).isPresent()) {
                System.out.println("Admin account already exists. Skipping initialization.");
                return;
            }

            User admin = new User(
                    "admin",
                    "Admin@123",
                    Role.ADMIN,
                    "System Administrator",
                    "9554054732",
                    Gender.MALE,
                    LocalDate.of(2004, 9, 6)
            );

            repo.save(admin, conn);

            System.out.println("SUCCESS: Super Admin created!");
            System.out.println("--------------------------------");
            System.out.println("Username: admin");
            System.out.println("Password: Admin@123");
            System.out.println("--------------------------------");

        } catch (SQLException | InvalidInputException e) {
            System.err.println("Seeding Failed: " + e.getMessage());
        }
    }
}