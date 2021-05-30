import axiosInstance from '../../auth/auth'
import Node from './Node'
const FormData = require('form-data');

const API_BASE_FILES = require('../../constants/index').default.API_BASE_FILES;
const API_FOLDER = require('../../constants/index').default.API_FOLDER;
const API_FILE = require('../../constants/index').default.API_FILE;
const API_CREATE = require('../../constants/index').default.API_CREATE;
const API_DELETE = require('../../constants/index').default.API_DELETE;
const API_SHARED = require('../../constants/index').default.API_SHARED;


const FOLDER = "folder"

export default class NodeRequester {
    static getRootForUser(nodesController){
        axiosInstance.get(API_BASE_FILES + API_FOLDER)
            .then(response => {
                nodesController.setNodes(response.data)
            })
    }

    static async addFile(parentId, file) {
        try {
            const form = new FormData();
            form.append('file', file);
            const config = { headers: { 
                'Content-Type': 'multipart/form-data',
            }};
    
            const response = await axiosInstance.post(
                API_BASE_FILES + API_FILE + API_CREATE + '/' + file.name + '/' + parentId, 
                form,
                config
            )
            return response;
        } catch (error) {
            return error.response
        }
    }

    static async addDirectory(directoryName, parentId) {
        try {
            const response = await axiosInstance.post(
                API_BASE_FILES + API_FOLDER + API_CREATE + '/' + directoryName + '/' + parentId)
            
            return response;
        } catch(error) {
            return error.response;
        }
    }

    static async removeNode(id) {
        const url = API_BASE_FILES + API_DELETE + '/' + id;
        return await axiosInstance.delete(url)
    }

    static async getFile(id, format) {
        const url = API_BASE_FILES + API_FILE + '/' + id;
        if (format.includes("image")){
            try {
                const response = await axiosInstance.get(url, { responseType: 'blob'})
                const imageUrl = URL.createObjectURL(response.data);
                return imageUrl
            } catch (error) {
                console.log(error)
            }
        } else if (format.includes("text")){
            try {
                const response = await axiosInstance.get(url)
                console.log(response)
                return response.data
            } catch (error) {
                console.log(error)
            }
        }
    }

    static async getFolder(id) {
        const url = API_BASE_FILES + API_FOLDER + '/' + id;
        const response = await axiosInstance.get(url)
        return response.data.content
    }

    static async getNode(id) {
        const url = API_BASE_FILES + API_FOLDER + '/' + id;
        const response = await axiosInstance.get(url)
        return response.data
    }

    static async getContentFromFolders(actualContent){
        if (actualContent !== undefined) {
            for (let node of actualContent){
                if (node.format === FOLDER){
                    const actualContent = await this.getFolder(node.id)
                    node.content = actualContent;
                }
            }
        } else {
            return;
        }
    }   
    
    static async getSharedNodes() {
        const url = API_BASE_FILES + API_SHARED
        const response = await axiosInstance.get(url)
        return response.data
    }
}