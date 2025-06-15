// Save this file at: src/routes/api/message/send/++server.ts

import { json, error } from '@sveltejs/kit';
import type { RequestHandler } from './$types';

export const POST: RequestHandler = async ({ locals, request, fetch }) => {
	if (!locals.user || !locals.user.token) {
		throw error(401, 'Unauthorized');
	}

	const { text, userId, ticketId } = await request.json();

	if (!text || !userId || !ticketId) {
		throw error(400, 'Missing required fields: text, userId, and ticketId are required.');
	}

	let backendUrl = `http://localhost:8080/api/messages?status=Switch`;

	const requestBody = {
		text:text,
		userId: userId,
		ticketId: ticketId,
	}

	console.log(JSON.stringify(requestBody));

	const response = await fetch(backendUrl, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'Authorization': `Bearer ${locals.user.token}`
		},
		body: JSON.stringify(requestBody)
	});

	console.log(response);

	if (!response.ok) {
		try {
			const errorBody = await response.json();
			throw error(response.status, errorBody.message || 'Failed to send the message via the backend.');
		} catch {
			throw error(response.status, `Backend responded with status: ${response.status}`);
		}
	}

	// 6. Return the response to the client. The backend returns the full list of messages.
	const messageList = await response.json();
	return json(messageList, { status: 201 });
};
