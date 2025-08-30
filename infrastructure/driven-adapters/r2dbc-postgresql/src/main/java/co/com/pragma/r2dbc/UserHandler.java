package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserHandler {

    private final UserUseCase userUseCase;
    private final TransactionalOperator transactionalOperator;

    public Mono<User> saveUser(User user) {
        return userUseCase.saveUser(user,null)
                .as(transactionalOperator::transactional);
    }
}