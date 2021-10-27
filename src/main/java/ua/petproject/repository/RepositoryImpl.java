package ua.petproject.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import ua.petproject.annotations.Column;
import ua.petproject.annotations.Entity;
import ua.petproject.annotations.Table;
import ua.petproject.util.DataBaseConnection;
import ua.petproject.util.PropertiesLoader;

import javax.persistence.Id;
import java.io.Closeable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RepositoryImpl<E, ID> implements Repository<E, ID>, Closeable {

    private final Connection connection;

    private final ObjectMapper objectMapper;
    private final Class<E> modelClass;
    private final Map<String, String> columnFieldName;
    private final PreparedStatement findAllStatement;
    private final PreparedStatement findByIdStatement;
    private final PreparedStatement deleteByIdStatement;
    private final PreparedStatement createStatement;
    private final PreparedStatement updateStatement;
    private String dataBaseName;
    private Field id;

    @SneakyThrows
    public RepositoryImpl(Class<E> modelClass) {
        this.connection = DataBaseConnection.getInstance().getConnection();
        this.dataBaseName = PropertiesLoader.getProperty("db.name");
        this.objectMapper = new ObjectMapper();

        this.modelClass = modelClass;
        this.columnFieldName = Arrays.stream(this.modelClass.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.getAnnotation(Id.class) != null)
                .collect(Collectors.toMap(field -> getColumnName(field), field -> field.getName()));
        String[] generatedColumns = {getColumnName(Arrays.stream(this.modelClass.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.getAnnotation(Id.class) != null)
                .findAny().orElseThrow(() -> new RuntimeException("Entity must contain ID")))};
        String tableName = modelClass.getAnnotation(Entity.class) != null ?
                modelClass.getAnnotation(Table.class).name() : modelClass.getSimpleName().toLowerCase();
        String countValues = IntStream.range(0, columnFieldName.size())
                .mapToObj(i -> "?")
                .collect(Collectors.joining(","));
        this.id = Arrays.stream(this.modelClass.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.getAnnotation(Id.class) != null)
                .findFirst().get();
        String id = getColumnName(this.id);


        String fieldsForCreate = columnFieldName.keySet().stream().collect(Collectors.joining(","));
        String fieldsForUpdate = columnFieldName.keySet().stream()
                .map(el -> el + "=?")
                .collect(Collectors.joining(","));

        this.findAllStatement = connection.prepareStatement("SELECT * FROM " + dataBaseName + "." + tableName, generatedColumns);
        this.createStatement = connection.prepareStatement("INSERT INTO " + dataBaseName + "." + tableName + "(" + fieldsForCreate + ")"
                + " VALUES (" + countValues + ")", generatedColumns);
        this.findByIdStatement = connection.prepareStatement("SELECT * FROM " + dataBaseName + "." + tableName + " WHERE " + id + "=?;");
        this.deleteByIdStatement = connection.prepareStatement("DELETE FROM " + dataBaseName + "." + tableName + " WHERE " + id + "=?;");
        this.updateStatement = connection.prepareStatement("UPDATE " + dataBaseName + "." + tableName + " SET " + fieldsForUpdate + " WHERE " + id + "=?;", generatedColumns);


    }

    private String getColumnName(Field field) {
        return field.getAnnotation(Column.class) == null ? field.getName() : field.getAnnotation(Column.class).name();
    }
    @SneakyThrows
    @Override
    public E save(E e) {
        if (findById((ID) id.get(e)).isPresent()) {
            return executeQuery(updateStatement,e);
        }
        return executeQuery(createStatement, e);
    }

    @Override
    public List<E> saveAll(Iterable<E> itrb) {
        List<E> result = new ArrayList<>();
        for (E e : itrb) result.add(save(e));
        return result;
    }

    @SneakyThrows
    @Override
    public void deleteById(ID id) {
        deleteByIdStatement.setObject(1, id);
        deleteByIdStatement.executeUpdate();
    }

    @SneakyThrows
    @Override
    public Optional<E> findById(ID id) {
        findByIdStatement.setObject(1, id);
        List<E> result = parse(findByIdStatement.executeQuery());
        if (result.isEmpty()) return Optional.empty();
        if (result.size() > 1) throw new RuntimeException("Method 'find by id' returned more than one result");
        return Optional.of(result.get(0));
    }

    @SneakyThrows
    @Override
    public List<E> findAll() {
        return parse(findAllStatement.executeQuery());
    }

    @SneakyThrows
    private E executeQuery(PreparedStatement statement, E e) {
        int count = 1;
        for (String fieldName : columnFieldName.values()) {
            Field declaredField = modelClass.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            statement.setObject(count++, declaredField.get(e));
        }
        statement.executeUpdate();
        ResultSet rs = statement.getGeneratedKeys();
        ID id = null;
        while (rs.next()) {
            id = (ID) rs.getObject(1);
        }
        return findById(id).get();
    }

    @SneakyThrows
    private List<E> parse(ResultSet resultSet) {
        final List<E> result = new ArrayList<>();
        while (resultSet.next()) {
            final Map<String, Object> objectMap = new HashMap<>();
            for (String fieldName : columnFieldName.keySet()) {
                objectMap.put(columnFieldName.get(fieldName), resultSet.getObject(fieldName));
            }
            result.add(objectMapper.convertValue(objectMap, modelClass));
        }
        return result;
    }

    @SneakyThrows
    @Override
    public void close() {
        connection.close();
    }
}
