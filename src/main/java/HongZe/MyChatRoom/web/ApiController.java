package HongZe.MyChatRoom.web;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import HongZe.MyChatRoom.entity.User;
import HongZe.MyChatRoom.service.UserService;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    UserService userService;

    @GetMapping("/users")
	public List<User> users() {
        return userService.getUsers();
    }

    @GetMapping("/users/{id}")
    public User user(@PathVariable("id") long id) {
        return userService.getUserById(id);
    }
}
