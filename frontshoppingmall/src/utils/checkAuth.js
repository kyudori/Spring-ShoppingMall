import axios from 'axios';

export async function checkAdmin() {
    try {
        const response = await axios.get(`${process.env.NEXT_PUBLIC_BACKEND_URL}/api/users/check-auth`, { withCredentials: true });
        const roles = response.data.authorities.map(authority => authority.authority);
        return roles.includes('ROLE_ADMIN');
    } catch (error) {
        console.error('Failed to check admin role:', error);
        return false;
    }
}
export default checkAdmin;
