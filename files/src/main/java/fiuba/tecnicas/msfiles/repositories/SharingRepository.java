package fiuba.tecnicas.msfiles.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import fiuba.tecnicas.msfiles.models.sharing.Share;

public interface SharingRepository extends JpaRepository<Share, Long> {
	public List<Share> deleteByUserIdAndNodeId(Long userId, Long nodeId);

	public List<Share> findByUserIdAndAcceptedAtIsNull(Long userId);

	public Optional<Share> findByUserIdAndNodeIdAndAcceptedAtIsNull(Long userId, Long nodeId);

	public List<Share> deleteByNodeId(Long nodeId);
}
