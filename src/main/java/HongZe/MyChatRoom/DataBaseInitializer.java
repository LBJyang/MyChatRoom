/**
 * 
 */
package HongZe.MyChatRoom;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * initialize the database
 * 
 * @author Yang
 */
@Component
public class DataBaseInitializer {
	@Autowired
	DataSource dataSource;

	@PostConstruct
	public void init() throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			try (var statement = connection.createStatement()) {
				statement.executeUpdate("""
						CREATE TABLE IF NOT EXISTS users (
						id BIGINT IDENTITY NOT NULL PRIMARY KEY,
						email VARCHAR(100) NOT NULL,
						password VARCHAR(100) NOT NULL,
						name VARCHAR(100) NOT NULL,
						createdAt BIGINT NOT NULL,
						UNIQUE (email))	""");
			}

		}

	}
}
