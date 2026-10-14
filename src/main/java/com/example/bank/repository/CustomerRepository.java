package com.example.bank.repository;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import java.util.Optional;

import com.example.bank.dto.CustomerResponse;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {

    private final DataSource dataSource;

    public CustomerRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long save(String name, String email) {
        String sql = "INSERT INTO customers (name, email) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, email);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new IllegalStateException("Insert succeeded but no id was returned");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save customer", e);
        }
    }
    public Optional<CustomerResponse> findById(long id) {
        String sql = "SELECT id, name, email FROM customers WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new CustomerResponse(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find customer " + id, e);
        }
    }
    public List<CustomerResponse> findAll() {
        String sql = "SELECT id, name, email FROM customers ORDER BY id";
        List<CustomerResponse> customers = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(new CustomerResponse(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email")));
            }
            return customers;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load customers", e);
        }
    }
    public boolean update(long id, String name, String email) {
        String sql = "UPDATE customers SET name = ?, email = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setLong(3, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update customer " + id, e);
        }
    }

    public boolean deleteById(long id) {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete customer " + id, e);
        }
    }
}