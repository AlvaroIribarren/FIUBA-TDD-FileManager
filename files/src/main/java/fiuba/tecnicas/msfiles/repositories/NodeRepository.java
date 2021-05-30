package fiuba.tecnicas.msfiles.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fiuba.tecnicas.msfiles.models.nodes.Node;

public interface NodeRepository extends JpaRepository<Node, Long> {
	public List<Node> findByOwnerIdAndNameContainingIgnoreCase(Long id, String name);
	
	public List<Node> findByParentId(Long parentId);

	public Optional<Node> findByOwnerIdAndFormatAndParentIdIsNull(Long ownerId, String format);

	@Query("SELECT n FROM Node n, Share s WHERE s.nodeId = n.id AND s.acceptedAt IS NOT NULL AND s.userId = :userId")
	public List<Node> findSharedNodes(@Param("userId") Long userId);

	public Optional<Node> findByOwnerIdAndNameAndParentId(Long id, String nodeName, Long parentId);
}
