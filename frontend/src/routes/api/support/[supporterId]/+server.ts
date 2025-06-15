import { json, error } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export interface TicketRating {
	/** The unique identifier for the rating itself. */
	ratingId: number;

	/** The comment or text feedback provided with the rating. */
	text: string;

	/** The numerical rating value (e.g., 5.0). */
	rating: number;

	/** The ID of the ticket that this rating pertains to. */
	ticketId: number;

	/** The numeric ID of the user who submitted the rating. */
	createdById: number;

	/** The numeric ID of the user who was rated (likely the support agent). */
	ratedUserId: number;

	/** The timestamp when the rating was created, as an ISO-like string. */
	createdAt: string;
}

export const GET: RequestHandler = async ({ locals, params, fetch }) => {

	if (!locals.user || !locals.user.token) {
		throw error(401, 'Unauthorized');
	}

	const supportId = params.supporterId;

	const response = await fetch(`http://localhost:8080/api/ratings/support/${supportId}`, {
		headers: {
			'Authorization': `Bearer ${locals.user.token}`
		}
	});

	if (!response.ok) {
		throw error(response.status, 'Failed to fetch messages from API');
	}

	const messages = await response.json();
	return json(messages);
};