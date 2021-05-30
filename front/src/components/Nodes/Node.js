export default class Node {
    constructor(id, name, size, owner, format, parentId, createdAt, updatedAt){
        this.id = id
        this.name = name
        this.size = size
        this.owner = owner
        this.format = format
        this.parentId = parentId
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.content = []
        this.sharing = []
    }

    getNodeFormat(){
        return this;
    }
}