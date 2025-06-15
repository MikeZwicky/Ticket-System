// @ts-nocheck
import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load = async ({ locals, cookies }: Parameters<PageServerLoad>[0]) => {
	const token = cookies.get('bearer_token');

	if (!token) {
		throw error(401, 'Unauthorized');
	}

	// @ts-ignore
	const userId = locals.user.userId;
	// @ts-ignore
	const userRole = locals.user.role;
	const status = 'open'
	let url = `http://localhost:8080/api/tickets/filter/${status}?userID=${userId}`;
	if (userRole == 'Admin') {
		url = "http://localhost:8080/api/tickets/all";
	}

	const response = await fetch(url, {
		headers: {
			'Authorization': `Bearer ${token}`,
		},
	});

	console.log(response)

	if (response.ok) {
		return {
			protectedData: await response.json(),
		};
	}

	console.log(response);
	return {
		protectedData: null,
	}
};