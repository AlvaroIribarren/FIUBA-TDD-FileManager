package fiuba.tecnicas.msfiles.models.sharing;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;

import fiuba.tecnicas.msfiles.models.User;
import fiuba.tecnicas.msfiles.models.nodes.Node;

@Entity
@Table(name = "user_nodes")
@IdClass(ShareKey.class)
public class Share implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotNull
	@Column(name = "user_id")
	private Long userId;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "user_id",
		referencedColumnName = "id",
		insertable = false, updatable = false
	)
	private User user;
	
	@Id
	@NotNull
	@Column(name = "node_id")
	private Long nodeId;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(
		name = "node_id",
		referencedColumnName = "id",
		insertable = false, updatable = false
	)
	private Node node;
	
	@NotNull
	@Column(name = "mode")
	private Character mode;
	
	@Column(name = "accepted_at")
	private Date acceptedAt;
	
	public static Share createAutoAccepted(Long userId, Long nodeId, Character shareMode) {
		Share autoAcceptedShare = new Share();
		autoAcceptedShare.setUserId(userId);
		autoAcceptedShare.setNodeId(nodeId);
		autoAcceptedShare.setMode(shareMode);
		autoAcceptedShare.setAcceptedAt(new Date(System.currentTimeMillis()));
		
		return autoAcceptedShare;
	}
	
	public Share() {
		
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
	
	public Character getMode() {
		return mode;
	}

	public void setMode(Character mode) {
		this.mode = mode;
	}
	
	public Date getAcceptedAt() {
		return acceptedAt;
	}
	
	public void setAcceptedAt(Date acceptedAt) {
		this.acceptedAt = acceptedAt;
	}
	
	public boolean readOnly() {
		return this.mode.equals(ShareModes.READ.getMode());
	}
	
	public boolean hasValidMode() {
		return this.mode != null && (this.mode == 'R' || this.mode == 'W');
	}

	public boolean isAccepted() {
		return this.getAcceptedAt() != null;
	}
}