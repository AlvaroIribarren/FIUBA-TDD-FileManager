package fiuba.tecnicas.msfiles.models.sharing;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.sun.istack.NotNull;

@Entity
public class ShareKey implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Column(name = "user_id")
	private Long userId;
	
	@Id
	@NotNull
	@Column(name = "node_id")
	private Long nodeId;
	
	public ShareKey() {
		
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		if (o.getClass() != this.getClass()) {
			return false;
		}
		
		final ShareKey otherSK = (ShareKey) o;
		
		return otherSK.getNodeId() == this.getNodeId() && otherSK.getUserId() == this.getUserId();
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(this.getUserId()) + Long.hashCode(this.getNodeId());
	}
}