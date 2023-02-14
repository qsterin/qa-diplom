package ru.netology.diplom.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.*;

public class SQLHelper {
    private static QueryRunner runner = new QueryRunner();

    private SQLHelper() {
    }

    @SneakyThrows
    private static Connection getConnection() {
        var path = System.getProperty("url");
        var loginUser = System.getProperty("userName");
        var loginPass = System.getProperty("password");
        return DriverManager.getConnection(path, loginUser, loginPass);
    }

    public static String getCardStatusPayment() {
        var statusSQL = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (var conn = getConnection()) {
            var result = runner.query(conn, statusSQL, new ScalarHandler<String>());
            return result;
        } catch (SQLException exception) {
            System.err.println("Транзакция не прошла");
        }
        return null;
    }

    public static String getCardStatusCredit() {
        var statusSQL = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        try (var conn = getConnection()) {
            var result = runner.query(conn, statusSQL, new ScalarHandler<String>());
            return result;
        } catch (SQLException exception) {
            System.err.println("Транзакция не прошла");
        }
        return null;
    }

    @SneakyThrows
    public static void cleanData() {
        var conn = getConnection();
        runner.execute(conn, "DELETE FROM payment_entity");
        runner.execute(conn, "DELETE FROM credit_request_entity");
    }
}
