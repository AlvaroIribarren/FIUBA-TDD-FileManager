import axiosInstance from '../../../../auth/auth';
import NodesRequester from '../../NodesRequester'

const API_USERS = require('../../../../constants/index').default.API_USERS;
const API_BASE_AUTH = require('../../../../constants/index').default.API_BASE_AUTH;


export default class UsersRequester {
    static async getUsers() {
        const url = API_BASE_AUTH + API_USERS
        const response = await axiosInstance.get(url)
        return response.data;
    }

    static async getUsersWhoAlreadyHaveThisFileShared(nodeId) {
        const node = await NodesRequester.getNode(nodeId)
        return node.sharing
    }
}