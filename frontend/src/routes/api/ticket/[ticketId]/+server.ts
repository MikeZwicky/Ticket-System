import { error, type RequestHandler } from '@sveltejs/kit';

/**
 * Handles DELETE requests to delete a specific ticket.
 * It takes a ticket ID from the URL and proxies the request to the backend.
 */
export const DELETE: RequestHandler = async ({ locals, params, fetch }) => {
	// 1. Authenticate the user to ensure they are logged in.
	if (!locals.user || !locals.user.token) {
		throw error(401, 'Unauthorized: You must be logged in to perform this action.');
	}

	// Optional but recommended: Authorize based on role.
	// if (locals.user.role !== 'Admin') {
	//     throw error(403, 'Forbidden: You do not have permission to delete tickets.');
	// }

	// 2. Get the ticket ID from the URL's dynamic parameter.
	const ticketId = params.ticketId;
	if (!ticketId) {
		throw error(400, 'Bad Request: A Ticket ID must be provided in the URL.');
	}

	// 3. Get the user ID from the authenticated user's session data.
	const userId = locals.user.userId;
	if (!userId) {
		throw error(400, 'Bad Request: User ID could not be determined from token.');
	}

	// 4. Construct the correct backend URL with the userId as a query parameter.
	const backendUrl = `http://localhost:8080/api/tickets/${ticketId}?userId=${userId}`;

	// 5. Forward the DELETE request to your Spring Boot backend.
	// Note: There is no 'body' in this request.
	const response = await fetch(backendUrl, {
		method: 'DELETE',
		headers: {
			// Securely add the authorization token.
			'Authorization': `Bearer ${locals.user.token}`
		}
	});

	// 6. Handle the response from the backend.
	if (!response.ok) {
		// If the backend returns an error (e.g., 404 Not Found or 403 Forbidden), pass it along.
		throw error(response.status, `Failed to delete ticket #${ticketId}.`);
	}

	// 7. Return a successful response to the client.
	// A 204 "No Content" status is standard and appropriate for a successful DELETE operation.
	return new Response(null, { status: 204 });
};