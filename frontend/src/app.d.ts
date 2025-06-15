// See https://svelte.dev/docs/kit/types#app.d.ts
// for information about these interfaces
import type { Ticket } from '$lib/type/ticketType';

declare global {
	namespace App {
		// interface Error {}
		interface Locals {
			user: {
				isAuthenticated: boolean,
				username: string,
				role: string,
				userId: number,
				token: string
			} | null;
			protectedData: [Ticket]
		}
		// interface PageData {}
		// interface PageState {}
		// interface Platform {}
	}
}

export {};
