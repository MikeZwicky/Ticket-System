import type { Handle } from '@sveltejs/kit';
import { jwtDecode } from 'jwt-decode';

interface AuthTokenPayload {
	sub: string;
	role: string;
	userId: number;
	iat: number;
	exp: number;
}

export const handle: Handle = async ({ event, resolve }) => {
	const token = event.cookies.get('bearer_token');
	event.locals.user = null; // Start with no user

	if (token) {
		try {
			const decoded = jwtDecode<AuthTokenPayload>(token);

			if (decoded.exp * 1000 > Date.now()) {
				event.locals.user = {
					isAuthenticated: true,
					username: decoded.sub,
					userId: decoded.userId,
					role: decoded.role,
					token: token
				};
			}
		} catch (error) {
			// Token is invalid, expired, or malformed.
			// `event.locals.user` will remain null.
			console.error('Invalid token:', error);
		}
	}

	return resolve(event);
};