import { json, error } from '@sveltejs/kit';
import type { RequestHandler } from './$types';
/**
 * Handles POST requests to create a new ticket.
 * This acts as a secure proxy to your backend API.
 */
export const POST: RequestHandler = async ({ locals, request, fetch }) => {
	// 1. Ensure the user is authenticated by checking the data populated by hooks.++server.ts
	if (!locals.user || !locals.user.token) {
		throw error(401, 'Unauthorized: You must be logged in to create a ticket.');
	}

	// 2. Get the ticket data from the incoming request body sent by the form
	const ticketData = await request.json();

	// 3. Forward the request to your Spring Boot backend at the /api/tickets endpoint
	const response = await fetch(`http://localhost:8080/api/tickets`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			// Securely add the bearer token for the backend request
			'Authorization': `Bearer ${locals.user.token}`
		},
		body: JSON.stringify(ticketData)
	});

	// 4. Handle the response from the backend
	if (!response.ok) {
		// If the backend returns an error, try to parse its message and forward it
		try {
			const errorBody = await response.json();
			throw error(response.status, errorBody.message || 'Failed to create the ticket on the backend.');
		} catch {
			// Fallback for non-JSON error responses
			throw error(response.status, `Backend responded with status: ${response.status}`);
		}
	}

	// 5. If successful, return the backend's response to the client
	const createdTicket = await response.json();
	return json(createdTicket, { status: 201 }); // 201 Created
};
