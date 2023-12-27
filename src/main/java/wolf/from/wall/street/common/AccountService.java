package wolf.from.wall.street.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wolf.from.wall.street.entity.Account;
import wolf.from.wall.street.repository.AccountRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Optional<Account> getAccountByResourceIdAndUserId(Integer resourceId, Long userId) {
        return accountRepository.getAccountByResourceIdAndUserId(resourceId, userId);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }
}
