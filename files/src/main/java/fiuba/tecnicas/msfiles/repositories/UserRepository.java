package fiuba.tecnicas.msfiles.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fiuba.tecnicas.msfiles.models.User;;

public interface UserRepository extends JpaRepository<User, Long> {
	public Optional<User> findByEmail(String email);
}
