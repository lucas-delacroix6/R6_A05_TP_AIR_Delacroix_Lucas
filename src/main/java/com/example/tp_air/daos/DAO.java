//package com.example.tp_air.daos;
//
//import java.sql.Connection;
//import java.util.List;
//
//public abstract class DAO<T> {
//
//    protected Connection connection;
//
//    public DAO(Connection connection) {
//        this.connection = connection;
//    }
//
//    public abstract boolean create(T obj);
//
//    public abstract T find(int id);
//
//    public abstract List<T> findAll();
//
//    public abstract boolean update(T obj);
//
//    public abstract boolean delete(int id);
//}
