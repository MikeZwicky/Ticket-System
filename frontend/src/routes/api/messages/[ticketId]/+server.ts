import { json, error } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export const GET: RequestHandler = async ({ locals, params, fetch }) => {
	// 1. Check if the user is authenticated
	if (!locals.user || !locals.user.token) {
		throw error(401, 'Unauthorized');
	}

	// 2. Get the ticketId from the URL (e.g., /api/messages/3)
	const ticketId = params.ticketId;

	// 3. Fetch data from your Spring Boot backend (server-to-server)
	const response = await fetch(`http://localhost:8080/api/messages/ticket/${ticketId}`, {
		headers: {
			'Authorization': `Bearer ${locals.user.token}`
		}
	});

	// 4. If the backend call fails, forward the error
	if (!response.ok) {
		throw error(response.status, 'Failed to fetch messages from API');
	}

	// 5. Get the data and return it as a JSON response to your SvelteKit client
	const messages = await response.json();
	return json(messages);
};