//package com.example.tp_air.daos;
//
//import com.example.tp_air.models.Annonce;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class AnnonceDAO extends DAO<Annonce> {
//
//    public AnnonceDAO(Connection connection) {
//        super(connection);
//    }
//
//    @Override
//    public boolean create(Annonce annonce) {
//        String sql = "INSERT INTO annonce (title, description, address, mail) VALUES (?, ?, ?, ?)";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setString(1, annonce.getTitle());
//            stmt.setString(2, annonce.getDescription());
//            stmt.setString(3, annonce.getAddress());
//            stmt.setString(4, annonce.getMail());
//
//            int rowsAffected = stmt.executeUpdate();
//
//            if (rowsAffected > 0) {
//                ResultSet rs = stmt.getGeneratedKeys();
//                if (rs.next()) {
//                    annonce.setId(rs.getInt(1));
//                }
//                return true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    @Override
//    public Annonce find(int id) {
//        String sql = "SELECT * FROM annonce WHERE id = ?";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, id);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return new Annonce(
//                            rs.getInt("id"),
//                            rs.getString("title"),
//                            rs.getString("description"),
//                            rs.getString("address"),
//                            rs.getString("mail"),
//                            rs.getTimestamp("date")
//                    );
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public List<Annonce> findAll() {
//        List<Annonce> annonces = new ArrayList<>();
//        String sql = "SELECT * FROM annonce ORDER BY date DESC";
//
//        try (Statement stmt = connection.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            while (rs.next()) {
//                annonces.add(new Annonce(
//                        rs.getInt("id"),
//                        rs.getString("title"),
//                        rs.getString("description"),
//                        rs.getString("address"),
//                        rs.getString("mail"),
//                        rs.getTimestamp("date")
//                ));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return annonces;
//    }
//
//    @Override
//    public boolean update(Annonce annonce) {
//        String sql = "UPDATE annonce SET title = ?, description = ?, address = ?, mail = ? WHERE id = ?";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setString(1, annonce.getTitle());
//            stmt.setString(2, annonce.getDescription());
//            stmt.setString(3, annonce.getAddress());
//            stmt.setString(4, annonce.getMail());
//            stmt.setInt(5, annonce.getId());
//
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    @Override
//    public boolean delete(int id) {
//        String sql = "DELETE FROM annonce WHERE id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, id);
//            return stmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//}