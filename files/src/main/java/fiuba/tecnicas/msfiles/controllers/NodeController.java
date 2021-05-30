package fiuba.tecnicas.msfiles.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fiuba.tecnicas.msfiles.models.Invitation;
import fiuba.tecnicas.msfiles.models.User;
import fiuba.tecnicas.msfiles.models.nodes.Node;
import fiuba.tecnicas.msfiles.models.sharing.Share;
import fiuba.tecnicas.msfiles.models.sharing.ShareModes;
import fiuba.tecnicas.msfiles.repositories.NodeRepository;
import fiuba.tecnicas.msfiles.repositories.SharingRepository;
import fiuba.tecnicas.msfiles.repositories.UserRepository;
import fiuba.tecnicas.msfiles.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import fiuba.tecnicas.msfiles.exceptions.AlreadySharedException;
import fiuba.tecnicas.msfiles.exceptions.DamagedNodeException;
import fiuba.tecnicas.msfiles.exceptions.FileSystemException;
import fiuba.tecnicas.msfiles.exceptions.CantUploadFileException;
import fiuba.tecnicas.msfiles.exceptions.InsufficientPermissionsException;
import fiuba.tecnicas.msfiles.exceptions.InvalidShareModeException;
import fiuba.tecnicas.msfiles.exceptions.InvitationNotFoundException;
import fiuba.tecnicas.msfiles.exceptions.MissingParameterException;
import fiuba.tecnicas.msfiles.exceptions.TokenErrorException;
import fiuba.tecnicas.msfiles.exceptions.UserNotFoundException;
import fiuba.tecnicas.msfiles.exceptions.NodeNotFoundException;
import fiuba.tecnicas.msfiles.exceptions.NodeNotPresentException;
import fiuba.tecnicas.msfiles.exceptions.NonSharedException;
import fiuba.tecnicas.msfiles.exceptions.RootFolderUnshareableException;

@RestController
@CrossOrigin
@RequestMapping("/nodes")
public class NodeController {
	private final NodeRepository nodeRepository;
	private final SharingRepository sharingRepository;
	private final UserRepository userRepository;
	
	@Autowired
	public NodeController(NodeRepository nodeRepository, SharingRepository sharingRepository, UserRepository userRepository) {
		this.nodeRepository = nodeRepository;
		this.sharingRepository = sharingRepository;
		this.userRepository = userRepository;
	}
	
	private User getUserFromToken(String bearer) throws TokenErrorException {
		if (bearer == null) {
			throw new TokenErrorException("Missing token");
		}
		
		if (!bearer.startsWith("Bearer ") ) {			
			throw new TokenErrorException("Malformed token");
		}
		
		String token = bearer.replace("Bearer ", "");
		
		JwtTokenUtil jtu = new JwtTokenUtil(this.userRepository);
		try {
			String email = jtu.getEmailFromToken(token);
			
			return this.userRepository.findByEmail(email).orElseThrow(() -> new TokenErrorException("Invalid token"));
		}
		//ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException
		catch (ExpiredJwtException e) {
			throw new TokenErrorException("Token expired");
		}
		catch (SignatureException e) {
			throw new TokenErrorException("Malformed or invalid token");
		}
		catch (MalformedJwtException e) {
			throw new TokenErrorException("Invalid token");			
		}
		
	}

	private Node getRootFolder(User user) {
		Node root = (Node) this.nodeRepository.findByOwnerIdAndFormatAndParentIdIsNull(user.getId(), Node.FOLDER_FORMAT).orElse(Node.createRootNode(user.getId()));
		
		if (root.getId() == null) {
			root = this.nodeRepository.save(root);
		}
		
		if (root.getOwnerId() == null) {
			root.setOwnerId(user.getId());
		}
		
		if (root.getOwner() == null) {
			root.setOwner(this.userRepository.findById(user.getId()).orElse(null));
		}

		return root;
	}
	
	@PostMapping(value = {"/file/create/{name}/{folderId}", "/file/create/{name}"})
	public Node createFile(@RequestHeader(name = "token", required = false) String token, @RequestParam("file") MultipartFile file, @PathVariable(value = "name") String nodeName, @PathVariable(value = "folderId", required = false) Optional<Long> folderId) throws TokenErrorException, MissingParameterException, CantUploadFileException, NodeNotFoundException, InsufficientPermissionsException {
		User requestUser = this.getUserFromToken(token);
		
		if (nodeName == null) {
			throw new MissingParameterException("Node name");
		}
		
		Long parentId = null;
		Node parent = null;
		if (folderId.isPresent()) {
			parentId = folderId.get();
			final Long fParentId = parentId;
			
			parent = (Node) this.nodeRepository.findById(parentId).orElseThrow(() -> new NodeNotFoundException(fParentId));
			
			if (!parent.isFolder()) {
				throw new CantUploadFileException("Parent node isn't a folder");
			}
			
			if (!parent.belongs(requestUser.getId()) && !parent.isSharedWith(requestUser.getId(), ShareModes.WRITE, true)) {
				throw new InsufficientPermissionsException(parentId, ShareModes.WRITE);
			}
		} 
		else {
			parent = getRootFolder(requestUser);
			parentId = parent.getId();
		}
		
		if (this.nodeRepository.findByOwnerIdAndNameAndParentId(requestUser.getId(), nodeName, parentId).isPresent()) {
			throw new CantUploadFileException("Node with that name already exists");
		}
			
		if (file == null || file.isEmpty()) {
			throw new MissingParameterException("Empty file");
		}
		
		Node node = new Node();
		node.setName(nodeName);
		node.setOwnerId(requestUser.getId());
		node.setParentId(parentId);
		node.setFormat(file.getContentType());
	
		try {
			node = this.nodeRepository.save(node);
			if (!node.save(file.getInputStream())) {
				this.nodeRepository.deleteById(node.getId());
				throw new FileSystemException("FileSystem can't save file");
			}
			
			node.setOwner(requestUser);

			return node;
		}
		catch (IOException e) {
			e.printStackTrace();
			this.nodeRepository.deleteById(node.getId());
			throw new CantUploadFileException("Error reading file");
		}
	}
	
	@PostMapping(value = {"/folder/create/{name}/{folderId}", "/folder/create/{name}"})
	public Node createFolder(@RequestHeader(name = "token", required = false) String token, @PathVariable(value = "name") String nodeName, @PathVariable(value = "folderId", required = false) Optional<Long> folderId) throws TokenErrorException, MissingParameterException, NodeNotFoundException, InsufficientPermissionsException {
		User requestUser = this.getUserFromToken(token);
		
		if (nodeName == null) {
			throw new MissingParameterException("Node name");
		}
		
		Long parentId = null;
		Node parent = null;
		if (folderId.isPresent()) {
			parentId = folderId.get();
			final Long fParentId = parentId;
			
			parent = (Node) this.nodeRepository.findById(parentId).orElseThrow(() -> new NodeNotFoundException(fParentId));
			
			if (!parent.isFolder()) {
				throw new CantUploadFileException("Parent node isn't a folder");
			}
			
			if (!parent.belongs(requestUser.getId()) && !parent.isSharedWith(requestUser.getId(), ShareModes.WRITE, true)) {
				throw new InsufficientPermissionsException(parentId, ShareModes.WRITE);
			}
		} 
		else {
			parent = getRootFolder(requestUser);
			parentId = parent.getId();
		}
		
		if (this.nodeRepository.findByOwnerIdAndNameAndParentId(requestUser.getId(), nodeName, parentId).isPresent()) {
			throw new CantUploadFileException("Node with that name already exists");
		}
			
		Node node = new Node();
		node.setName(nodeName);
		node.setOwnerId(requestUser.getId());
		node.setParentId(parentId);
		node.setFormat(Node.FOLDER_FORMAT);
		
		Node newNode = this.nodeRepository.save(node);
		
		if (parent != null && parent.getOwnerId() != requestUser.getId()) {
			Share shareToParentOwner = Share.createAutoAccepted(requestUser.getId(), newNode.getId(), ShareModes.WRITE.getMode());
			
			this.sharingRepository.save(shareToParentOwner);
		}
		
		newNode.setOwner(requestUser);
		
		return newNode;
	}
	
	@Transactional
	@DeleteMapping("/delete/{id}")
	public Node delete(@RequestHeader(name = "token", required = false) String token, @PathVariable("id") Long id) throws TokenErrorException, NodeNotFoundException, InsufficientPermissionsException {
		User requestUser = this.getUserFromToken(token);
		
		Node nodeToDelete = this.nodeRepository.findById(id).orElseThrow(() -> new NodeNotFoundException(id));
		
		if (!nodeToDelete.belongs(requestUser.getId())) {
			throw new InsufficientPermissionsException(nodeToDelete.getId(), ShareModes.WRITE);
		}
		
		if (!nodeToDelete.delete(this.nodeRepository, this.sharingRepository)) {
			throw new FileSystemException("Can't delete from FileSystem");
		}		
		
		return nodeToDelete;
	}
	
	//Solo busca dentro de archivos propios
	@GetMapping("/search/{name}")
	public List<Node> search(@RequestHeader(name = "token", required = false) String token, @PathVariable(value = "name") String name) {
		User requestUser = this.getUserFromToken(token);
		
		if (name == null) {
			return Collections.emptyList();
		}
		
		return this.nodeRepository.findByOwnerIdAndNameContainingIgnoreCase(requestUser.getId(), name);
	}
	
	@GetMapping("/file/{id}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@RequestHeader(name = "token", required = false) String token, @PathVariable(value = "id") Long id) throws TokenErrorException, InsufficientPermissionsException, DamagedNodeException, NodeNotPresentException {
		User requestUser = this.getUserFromToken(token);
		
		Optional<Node> node = this.nodeRepository.findById(id);
		
		if (!node.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		try {
			Node file = (Node) node.get();
			
			if (!file.belongs(requestUser.getId()) && !this.isNodeWithinSharedFolderForUser(file, requestUser.getId(), false)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			
			Resource res = file.get();
			
			if (res == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
			
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(file.getFormat())).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName()).body(res);
		}
		catch (ClassCastException e) {
			e.printStackTrace();
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@GetMapping(value = {"/folder/{id}", "/folder"})
	public Node getFolder(@RequestHeader(name = "token", required = false) String token, @PathVariable(value = "id", required = false) Optional<Long> id) throws TokenErrorException, NodeNotFoundException, InsufficientPermissionsException {
		User requestUser = this.getUserFromToken(token);
		
		try {
			Node folder = null;
			
			if (id.isPresent()) {
				folder = (Node) this.nodeRepository.findById(id.get()).orElseThrow(() -> new NodeNotFoundException(id.get()));
			}
			else {				
				folder = getRootFolder(requestUser);
			}
			
			if (!folder.belongs(requestUser.getId()) && !this.isNodeWithinSharedFolderForUser(folder, requestUser.getId(), false)) {
				throw new InsufficientPermissionsException(folder.getId(), ShareModes.READ);
			}
			
			folder.setContent(this.nodeRepository.findByParentId(folder.getId()));

			folder.getContent().forEach(node -> {
				if (node.getContent() == null) {
					node.setContent(new ArrayList<>());
				}
			});
			
			return folder;
		}
		catch (ClassCastException ce) {
			throw new NodeNotFoundException(id.isPresent() ? id.get() : null);
		}		
	}
	
	@GetMapping("/shared")
	public List<Node> sharedNodes(@RequestHeader(name = "token", required = false) String token) throws TokenErrorException {
		User requestUser = this.getUserFromToken(token);
		
		return this.nodeRepository.findSharedNodes(requestUser.getId());
	}
	
	@PostMapping("/share")
	public Share shareNode(@RequestHeader(name = "token", required = false) String token, @RequestBody Share share) throws TokenErrorException, NodeNotFoundException, InsufficientPermissionsException, AlreadySharedException, RootFolderUnshareableException {
		User requestUser = this.getUserFromToken(token);
		
		Node node = this.nodeRepository.findById(share.getNodeId()).orElseThrow(() -> new NodeNotFoundException(share.getNodeId()));
		User sharedUser = this.userRepository.findById(share.getUserId()).orElseThrow(() -> new UserNotFoundException(share.getUserId()));
		
		if (node.getParentId() == null) {			
			throw new RootFolderUnshareableException();
		}
		
		//Token -> Usuario que hace el request -> post auth
		if (!node.belongs(requestUser.getId())) {
			throw new InsufficientPermissionsException(requestUser.getId(), ShareModes.WRITE);
		}
		
		if (node.isSharedWith(share.getUserId(), null, false)) {
			throw new AlreadySharedException(share.getNodeId(), share.getUserId());
		}
		
		if (!share.hasValidMode()) {			
			throw new InvalidShareModeException(share.getMode());
		}
		
		Share s = this.sharingRepository.save(share);
		
		s.setUser(sharedUser);
		s.setNode(node);
		
		return s;
	}
	
	@Transactional
	@PostMapping("/unshare")
	public Share unShareNode(@RequestHeader(name = "token", required = false) String token, @RequestBody Share share) throws TokenErrorException, NodeNotFoundException, InsufficientPermissionsException, AlreadySharedException, RootFolderUnshareableException {
		User requestUser = this.getUserFromToken(token);
		
		Node node = this.nodeRepository.findById(share.getNodeId()).orElseThrow(() -> new NodeNotFoundException(share.getNodeId()));
		
		if (node.getParentId() == null) {			
			throw new RootFolderUnshareableException();
		}
		
		//Token -> Usuario que hace el request -> post auth
		if (!node.belongs(requestUser.getId())) {
			throw new InsufficientPermissionsException(requestUser.getId(), ShareModes.WRITE);
		}
		
		List<Share> deletedShares = this.sharingRepository.deleteByUserIdAndNodeId(share.getUserId(), share.getNodeId());
		
		if (deletedShares.size() == 0) {
			throw new NonSharedException(share.getNodeId(), share.getUserId());
		}
		
		return deletedShares.get(0);
	}
	
	@GetMapping("/invitations")
	public List<Share> getUserInvitations(@RequestHeader(name = "token", required = false) String token) throws TokenErrorException {
		User requestUser = this.getUserFromToken(token);
		
		return this.sharingRepository.findByUserIdAndAcceptedAtIsNull(requestUser.getId());
	}
	
	@Transactional
	@PostMapping("/invitation/attend")
	public Share attendInvitation(@RequestHeader(name = "token", required = false) String token, @RequestBody Invitation invitation) throws TokenErrorException {
		User requestUser = this.getUserFromToken(token);
		
		invitation.setUserId(requestUser.getId());
		
		Share share = this.sharingRepository.findByUserIdAndNodeIdAndAcceptedAtIsNull(invitation.getUserId(), invitation.getNodeId()).orElseThrow(() -> new InvitationNotFoundException(invitation.getUserId(), invitation.getNodeId()));
		
		if (invitation.isAccept()) {
			share.setAcceptedAt(new Date(System.currentTimeMillis()));
		}
		else {
			return this.sharingRepository.deleteByUserIdAndNodeId(invitation.getUserId(), invitation.getNodeId()).get(0);
		}
		
		return share;
	}
	
	protected boolean isNodeWithinSharedFolderForUser(Node node, Long userId, boolean withAcceptedInvitation) {
		if (node.isSharedWith(userId, null, withAcceptedInvitation)) {
			return true;
		}
		
		if (node.getParentId() == null) {
			return false;
		} else {
			Node parent = this.nodeRepository.findById(node.getParentId()).orElseGet(null);
			
			while (parent != null && parent.getParentId() != null) {
				if (parent.isSharedWith(userId, null, withAcceptedInvitation)) {
					return true;
				}
				
				parent = this.nodeRepository.findById(parent.getParentId()).orElseGet(null);
			}
		}
		
		return false;
	}
}
