import axiosInstance from '../../auth/auth'

const API_BASE_FILES = require('../../constants/index').default.API_BASE_FILES;
const API_SHARE = require('../../constants/index').default.API_SHARE;
const API_UNSHARE = require('../../constants/index').default.API_UNSHARE;
const API_INVITATIONS = require('../../constants/index').default.API_INVITATIONS;
const API_ATTEND = require('../../constants/index').default.API_ATTEND;
const API_SHARED = require('../../constants/index').default.API_SHARED;

const READ = 'R'
const WRITE = 'W'

function getReadOrWrite(writePermission) {
    if (writePermission) {
        return WRITE
    } else {
        return READ
    }
}

export default class InvitationsRequester {
    static async getInvitations() {
        const url = API_BASE_FILES + API_INVITATIONS;
        const response = await axiosInstance.get(url)
        return response.data;
    }

    static async share(nodeId, userId, writePermission){
        const url = API_BASE_FILES + API_SHARE;
        const mode = getReadOrWrite(writePermission);
        const body = {
            nodeId,
            userId,
            mode
        }
        try {
            const response = await axiosInstance.post(url, body)
            return response;
        } catch(error){
            console.log(error)
        }
    }

    
    static async unshare(nodeId, userId, writePermission) {
        const url = API_BASE_FILES + API_UNSHARE;
        const mode = getReadOrWrite(writePermission);
        const body = {
            nodeId,
            userId,
            mode
        }
        try {
            const response = await axiosInstance.post(url, body)
            return response;
        } catch(error){
            console.log(error)
        }
    }

    static async acceptInvitation(nodeId, isAccepted) {
        const url = API_BASE_FILES + API_ATTEND;
        const body = {
            nodeId,
            accept: isAccepted
        }
        try {
            const response = await axiosInstance.post(url, body)
            return response;
        } catch(error) {
            return error.response;
        }
    }

}
