package HongZe.MyChatRoom.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import HongZe.MyChatRoom.entity.User;
import HongZe.MyChatRoom.orm.MyTemplate;


@Component
public class UserService {
	final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	MyTemplate myTemplate;

	RowMapper<User> userRowMapper = new BeanPropertyRowMapper<>(User.class);

	public List<User> getUsers() {
		return myTemplate.fetchAll(User.class);
	}

	public User getUserById(long id) {
		return myTemplate.fetch(User.class, id);
	}

	public User getUserByEmail(String email) {
		return myTemplate.from(User.class).where("email = ?", email).first();
	}

	public User signin(String email, String password) {
		logger.info("try login by {}...", email);
		User user = getUserByEmail(email);
		if (user.getPassword().equals(password)) {
			return user;
		}
		throw new RuntimeException("login failed.");
	}

	public User register(String email, String password, String name) {
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		user.setName(name);
		user.setCreatedAt(System.currentTimeMillis());
		myTemplate.insert(user);
		return user;
	}

	public void updateUser(User user) {
		myTemplate.update(user);
	}
}
