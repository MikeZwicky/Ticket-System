import { redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

export const actions: Actions = {
	default: async ({ request, cookies }) => {
		const data = await request.formData();
		const username = data.get('username');
		const password = data.get('password');

		const requestBody = {
			login: username,
			password: password
		};

		const response = await fetch('http://localhost:8080/api/login', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(requestBody),
		});

		if (response.ok) {
			const token = await response.text()

			if (token) {
				cookies.set('bearer_token', token, {
					path: '/',
					httpOnly: true,
					secure: false,
					sameSite: 'strict',
					maxAge: 60 * 60 * 24 * 7,
				});

				throw redirect(303, '/dashboard');
			}
		}
		return {
			status: 401,
			body: { message: 'Invalid credentials' },
		};
	},
};