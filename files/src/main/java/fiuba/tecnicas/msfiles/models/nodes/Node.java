package fiuba.tecnicas.msfiles.models.nodes;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;

import fiuba.tecnicas.msfiles.exceptions.CantDeleteRootFolderException;
import fiuba.tecnicas.msfiles.filesystem.FileSystem;
import fiuba.tecnicas.msfiles.models.User;
import fiuba.tecnicas.msfiles.models.sharing.Share;
import fiuba.tecnicas.msfiles.models.sharing.ShareModes;
import fiuba.tecnicas.msfiles.repositories.NodeRepository;
import fiuba.tecnicas.msfiles.repositories.SharingRepository;

@Entity
@Table(name = "nodes")
public class Node {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	@NotNull
	protected Long id;
	
	@Column(name = "format")
	@Nullable
	protected String format;
	
	@Column(name = "name")
	@NotNull
	protected String name;
	
	@Column(name = "owner_id")
	@NotNull
	@JsonIgnore
	protected Long ownerId;

	public static String FOLDER_FORMAT = "folder";
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "owner_id",
		referencedColumnName = "id",
		insertable = false, updatable = false
	)
	protected User owner;
	
	@Column(name = "parent_id")
	protected Long parentId;
	
	@Column(name = "created_at")
	@CreationTimestamp
	@NotNull
	protected Date createdAt;
	
	@Column(name = "updated_at")
	@UpdateTimestamp
	@NotNull
	protected Date updatedAt;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "node"})
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(
		name = "node_id",
		referencedColumnName = "id"
	)
	protected List<Share> sharing;
	
	@Transient
	private List<Node> content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<Share> getSharing() {
		return sharing;
	}

	public void setSharing(List<Share> sharing) {
		this.sharing = sharing;
	}
	
	public boolean belongs(Long userId) {
		return this.ownerId == userId;
	}
	
	public boolean isSharedWith(Long userId, ShareModes mode, boolean withAcceptedInvitation) {
		for (Share s: this.sharing) {
			if (s.getUserId() == userId && (s.isAccepted() || !withAcceptedInvitation) && (mode == null || (s.getMode().equals(s.getMode()) || (s.getMode().equals(ShareModes.WRITE.getMode()) && mode.equals(ShareModes.READ))))) {
				return true;
			}
		}
		
		return false;
	}

	public List<Node> getContent() {
		return content;
	}

	public void setContent(List<Node> content) {
		this.content = content;
	}
	
	public static Node createRootNode(Long ownerId) {
		Node root = new Node();
		
		root.setFormat(Node.FOLDER_FORMAT);
		root.setName("root");
		root.setOwnerId(ownerId);
		Date creationTime = new Date(System.currentTimeMillis());
		root.setCreatedAt(creationTime);
		root.setUpdatedAt(creationTime);
		
		return root;
	}
	
	public Resource get() {
		if (format != FOLDER_FORMAT) {
			if (!FileSystem.getFileSystem().fileExists(this.id)) {
				return null;
			}
			
			return FileSystem.getFileSystem().getFile(this.id);
		} else {
			return null;
		}
	}
	
	public boolean save(InputStream in) {
		if (format != FOLDER_FORMAT) {
			return FileSystem.getFileSystem().saveFile(in, this.id);
		} else {
			return false;
		}
	}
	
	public boolean delete(NodeRepository nodeRepository, SharingRepository sharingRepository) throws CantDeleteRootFolderException {
		if (format.matches(FOLDER_FORMAT)) {
			if (this.parentId == null) {
				throw new CantDeleteRootFolderException();
			}
			
			List<Node> siblings = nodeRepository.findByParentId(this.id);
			for (Node n: siblings) {
				n.delete(nodeRepository, sharingRepository);
			}
			sharingRepository.deleteByNodeId(this.id);
			sharingRepository.flush();
			nodeRepository.deleteById(this.id);
			
			return true;
		} 
		else {
			if (FileSystem.getFileSystem().deleteFile(this.id)) {
				sharingRepository.deleteByNodeId(this.id);
				sharingRepository.flush();
				nodeRepository.deleteById(id);
				
				return true;
			}
			
			return false;
		}
	}
	
	public boolean isFolder() {
		return this.format.equals(FOLDER_FORMAT);
	}
	
	@Override
	public String toString() {
		return String.format("Id: %d, Name: %s, Owner: %d", this.id, this.name, this.ownerId);
	}
}
