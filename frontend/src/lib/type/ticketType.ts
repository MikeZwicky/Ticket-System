export type TicketPriority = 'High' | 'Medium' | 'Low'

export type TicketStatus = 'Open' | 'Closed' | 'Pending'

export type Ticket = {
	assignedToId: number;
	createdAt: string,
	createdById: number,
	description: string,
	priority: TicketPriority,
	statusForAssigned: TicketStatus,
	statusForCreator: TicketStatus,
	ticketId: number,
	title: string,
}