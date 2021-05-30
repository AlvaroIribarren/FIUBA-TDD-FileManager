package fiuba.tecnicas.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fiuba.tecnicas.auth.domain.Usuario;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(@Param("email") String userName);
}