package sopt.org.umbba.api.config.jwt;

import org.springframework.data.repository.CrudRepository;
import sopt.org.umbba.domain.domain.redis.RefreshToken;

//Redis에 저장해주는 역할
public interface TokenRepository extends CrudRepository<RefreshToken, Long> {
}
