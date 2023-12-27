package wolf.from.wall.street.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wolf.from.wall.street.entity.User;
import wolf.from.wall.street.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    @Value("${app.new.user.initial.balance}")
    private Integer initialBalance;

    public User createIfAbsent(Long userId, String userName) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        User user = new User();
        user.setUserName(userName);
        user.setUserId(userId);
        user.setBalance(initialBalance);
        user.setLastBoughDate(LocalDate.of(2000, 1, 1));

        return userRepository.save(user);
    }

    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
