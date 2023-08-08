package sopt.org.umbba.domain.domain.redis;

import org.springframework.data.repository.CrudRepository;

//Redis에 저장해주는 역할
public interface TokenRepository extends CrudRepository<RefreshToken, Long> {
}
