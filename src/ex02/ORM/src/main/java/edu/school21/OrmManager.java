package edu.school21;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class OrmManager {
    Connection connection;

    public OrmManager(String databaseName) {
        try {
            connection = DriverManager.getConnection(databaseName, "postgres", "postgres");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initialize(Class<?>... ormClasses) throws SQLException {
        dropTable(ormClasses);
        if (connection == null) return;
        for (Class<?> clazz : ormClasses) {
            OrmEntity tableName = clazz.getAnnotation(OrmEntity.class);
            if (tableName == null) break;
            StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName.table()).append(" (\n");
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getAnnotation(OrmColumnId.class) != null) {
                    String request = field.getName() + " SERIAL PRIMARY KEY,\n";
                    stringBuilder.append(request);
                } else {
                    OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
                    String request = parseRequest(field.getType(), ormColumn.length());
                    stringBuilder.append(ormColumn.name()).append(" ").append(request).append(",\n");
                }
            }
            stringBuilder.setLength(stringBuilder.length() - 2);
            stringBuilder.append("\n);");

            System.out.println(stringBuilder);

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(stringBuilder.toString());
            }
        }
    }

    public void save(Object entity) throws IllegalAccessException, SQLException {
        if (connection == null) return;
        Class<?> clazz = entity.getClass();
        if(clazz.isAnnotationPresent(OrmEntity.class)) {
            OrmEntity ormEntity = clazz.getAnnotation(OrmEntity.class);
            String tableName = ormEntity.table();
            StringBuilder stringBuilder = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
            StringBuilder stringBuilderValues = new StringBuilder();
            List<Object> list = new ArrayList<>();
            for(Field field : clazz.getDeclaredFields()) {
                if(!field.isAnnotationPresent(OrmColumn.class)) continue;
                field.setAccessible(true);
                list.add(field.get(entity));
                OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
                stringBuilder.append(ormColumn.name()).append(", ");
                stringBuilderValues.append("?, ");
            }
            stringBuilder.setLength(stringBuilder.length() - 2);
            stringBuilderValues.setLength(stringBuilderValues.length() - 2);
            stringBuilder.append(") VALUES (").append(stringBuilderValues).append(")");
            System.out.println(stringBuilder);
            try (PreparedStatement preparedStatement = connection.prepareStatement(stringBuilder.toString())) {
                int index = 1;
                for (Object object : list) {
                    preparedStatement.setObject(index++, object);
                }
                preparedStatement.executeUpdate();
            }
        }
    }

    public void update(Object entity) throws IllegalAccessException, SQLException {
        if (connection == null) return;
        Class<?> clazz = entity.getClass();
        if (clazz.isAnnotationPresent(OrmEntity.class)) {
            OrmEntity ormEntity = clazz.getAnnotation(OrmEntity.class);
            StringBuilder stringBuilder = new StringBuilder("UPDATE ").append(ormEntity.table()).append(" SET\n");
            Long id = null;
            String idName = null;
            List<Object> setObjects = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(OrmColumnId.class)) {
                    id = (Long) field.get(entity);
                    idName = field.getName();
                } else if (field.isAnnotationPresent(OrmColumn.class)) {
                    OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
                    Object fieldColumnValue = field.get(entity);
                    stringBuilder.append(ormColumn.name()).append(" = ?,\n");
                    setObjects.add(fieldColumnValue);
                }
            }
            stringBuilder.setLength(stringBuilder.length() - 2);
            stringBuilder.append("\nWHERE ").append(idName).append(" = ?\n");
            System.out.println(stringBuilder);
            try (PreparedStatement statement = connection.prepareStatement(stringBuilder.toString())) {
                int index = 1;
                for(Object entry : setObjects) {
                    statement.setObject(index, entry);
                    ++index;
                }
                if(id != null) statement.setLong(index, id);
                statement.executeUpdate();
            }
        }
    }

    public <T> T findById(Long id, Class<T> aClass) throws SQLException {
        if (connection == null || !aClass.isAnnotationPresent(OrmEntity.class)) return null;
        T resultObject = null;
        OrmEntity classAnnotation = aClass.getAnnotation(OrmEntity.class);
        String tableName = classAnnotation.table();
        String request = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(request)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    try {
                        resultObject = aClass.newInstance();
                        for (Field field : aClass.getDeclaredFields()) {
                            field.setAccessible(true);
                            if (field.isAnnotationPresent(OrmColumnId.class)) {
                                field.set(resultObject, resultSet.getLong(field.getName()));
                            } else if (field.isAnnotationPresent(OrmColumn.class)) {
                                OrmColumn ormColumn = field.getAnnotation(OrmColumn.class);
                                field.set(resultObject, resultSet.getObject(ormColumn.name()));
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }
        return resultObject;
    }

    private void dropTable(Class<?>... ormClasses) {
        for(Class<?> ormClass : ormClasses) {
            if(ormClass.isAnnotationPresent(OrmEntity.class)) {
                OrmEntity ormEntity = ormClass.getAnnotation(OrmEntity.class);
                String tableName = ormEntity.table();
                String dropTableQuery = "DROP TABLE IF EXISTS " + tableName;
                try (PreparedStatement preparedStatement = connection.prepareStatement(dropTableQuery)){
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private String parseRequest(Class<?> classType, int length) {
        String result;
        if (classType == String.class) {
            result = "VARCHAR(" + Math.max(length, 1) + ")";
        } else if (classType == Boolean.class) {
            result = "BOOLEAN";
        } else if (classType == Integer.class) {
            result = "INTEGER";
        } else if (classType == Double.class) {
            result = "DOUBLE PRECISION";
        } else if (classType == Long.class) {
            result = "BIGINT";
        } else {
            result = "ERROR TYPE";
        }
        return result;
    }
}
