<script lang="ts">
    import type { PageData } from './$types';
    import type { TicketSummary as Ticket, TicketPrioritySummary, TicketStatusSummary } from '$lib/types';

    // Define the type for a single chat message
    interface ChatMessage {
        messageId: number;
        ticketId: number;
        userId: number;
        text: string;
        createdAt: string;
        statusForAssigned: TicketStatusSummary;
        statusForCreator: TicketStatusSummary;
    }

    let {data} = $props()

    // --- State for Modals ---
    let selectedTicket: Ticket | null = $state(null);
    let isChatModalOpen = $state(false);
    let chatMessages: ChatMessage[] = $state([]);
    let isLoadingChat = $state(false);
    let chatError: string | null = $state(null);

    // --- State for New Ticket Form ---
    let newTicket = $state({
        title: '',
        description: '',
        priority: 'Medium' as TicketPrioritySummary
    })
    let isSubmittingTicket = $state(false);
    let ticketFormMessage: { type: 'success' | 'error', text: string } | null = $state(null);
    // --- State for New Chat Message ---
    let newMessageText = $state('');
    let isSendingMessage = $state(false);

    // --- Logic for Creating a New Ticket ---
    async function handleCreateTicket() {
        if (!data.user) return;
        isSubmittingTicket = true;
        ticketFormMessage = null;

        try {
            const response = await fetch('/api/ticket/create', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    ...newTicket,
                    createdById: data.user.userId
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Failed to create ticket.');
            }

            ticketFormMessage = { type: 'success', text: 'Ticket created successfully! The page will now refresh.' };
            newTicket = { title: '', description: '', priority: 'Medium' };
            setTimeout(() => window.location.reload(), 1500);

        } catch (err) {
            if (err instanceof Error) {
                ticketFormMessage = { type: 'error', text: err.message };
            } else {
                ticketFormMessage = { type: 'error', text: 'An unknown error occurred.' };
            }
        } finally {
            isSubmittingTicket = false;
        }
    }

    // --- Logic for Sending a Chat Message ---
    async function handleSendMessage() {
        if (!newMessageText.trim() || !selectedTicket || !data.user) return;
        isSendingMessage = true;

        try {
            const response = await fetch('/api/messages/send', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    text: newMessageText,
                    userId: data.user.userId,
                    ticketId: selectedTicket.ticketId
                })
            });

            if (!response.ok) {
                throw new Error('Failed to send message.');
            }

            const sentMessage: ChatMessage[] = await response.json();
            const actuallySendMessage : ChatMessage = sentMessage.at(sentMessage.length-1)

            chatMessages = [...chatMessages, actuallySendMessage];
            newMessageText = '';

        } catch (err) {
            // You can add more specific error handling here if needed
            console.error(err);
        } finally {
            isSendingMessage = false;
        }
    }


    // --- Modal and Formatting Functions ---
    function handleRowClick(ticket: Ticket) {
        selectedTicket = ticket;
    }

    function closeDetailsModal() {
        selectedTicket = null;
    }

    async function openChatModal(ticketId: number) {
        isLoadingChat = true;
        chatError = null;
        chatMessages = [];

        try {
            const response = await fetch(`/api/messages/${ticketId}`);
            if (!response.ok) throw new Error('Failed to load chat history.');
            chatMessages = await response.json();
            isChatModalOpen = true;
        } catch (error) {
            if (error instanceof Error) chatError = error.message;
            else chatError = 'An unknown error occurred.';
            isChatModalOpen = true;
        } finally {
            isLoadingChat = false;
        }
    }

    function closeChatModal() {
        isChatModalOpen = false;
        setTimeout(() => {
            chatMessages = [];
            chatError = null;
        }, 300);
    }

    function getPriorityClass(priority: TicketPrioritySummary): string {
        switch (priority) {
            case 'High': return 'badge-error';
            case 'Medium': return 'badge-warning';
            case 'Low': return 'badge-info';
            default: return 'badge-ghost';
        }
    }

    function getStatusClass(status: TicketStatusSummary): string {
        switch (status) {
            case 'Open': return 'badge-success';
            case 'Pending': return 'badge-warning';
            case 'Closed': return 'badge-neutral';
            default: return 'badge-ghost';
        }
    }

    function formatDate(dateString: string): string {
        return new Date(dateString).toLocaleString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }
    async function handleDelete(ticketId : number) {
        if (!confirm('Are you sure you want to delete this ticket?')) {
            return;
        }

        const response = await fetch(`/api/ticket/${ticketId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('Ticket deleted successfully!');
        } else {
            alert('Failed to delete ticket.');
        }
    }
</script>

<div class="p-4 sm:p-6 lg:p-8 bg-base-200 min-h-screen">
    {#if data.user}
        <h1 class="text-4xl m-2">Welcome {data.user.username}</h1>

        {#if data.protectedData}
            <!-- User role-based view -->
            {#if data.user.role === 'Admin' || data.user.role === 'Support'}
                <p class="text-2xl m-2">Ticket administration overview</p>
                <!-- Admin Table View -->
                <div class="overflow-x-auto shadow-xl rounded-lg">
                    <table class="table table-zebra w-full">
                        <thead class="bg-base-300 text-base-content">
                        <tr>
                            <th>Ticket ID</th>
                            <th>Title</th>
                            <th>Priority</th>
                            <th>Status (Assigned)</th>
                            <th>Created At</th>
                        </tr>
                        </thead>
                        <tbody>
                        {#if data.protectedData.length > 0}
                            {#each data.protectedData as ticket (ticket.ticketId)}
                                <tr class="hover cursor-pointer" onclick={() => handleRowClick(ticket)}>
                                    <th class="font-mono text-sm">#{ticket.ticketId}</th>
                                    <td>
                                        <div class="font-bold">{ticket.title}</div>
                                        <div class="text-sm opacity-60 truncate max-w-xs">{ticket.description}</div>
                                    </td>
                                    <td><span class="badge {getPriorityClass(ticket.priority)} text-white font-semibold">{ticket.priority}</span></td>
                                    <td><span class="badge {getStatusClass(ticket.statusForAssigned)} font-semibold">{ticket.statusForAssigned}</span></td>
                                    <td>{formatDate(ticket.createdAt)}</td>
                                </tr>
                            {/each}
                        {/if}
                        </tbody>
                    </table>
                </div>
            {:else if data.user.role === 'User'}
                <!-- Create Ticket Form for 'User' role -->
                <div class="max-w-2xl mx-auto my-8">
                    <div class="card bg-base-100 shadow-xl">
                        <div class="card-body">
                            <h2 class="card-title">Create a New Support Ticket</h2>
                            <form onsubmit={handleCreateTicket}>
                                <div class="form-control w-full">
                                    <label class="label" for="title"><span class="label-text">Title</span></label>
                                    <input type="text" id="title" bind:value={newTicket.title} placeholder="e.g., Website is down" class="input input-bordered w-full" required />
                                </div>
                                <div class="form-control w-full">
                                    <label class="label" for="description"><span class="label-text">Description</span></label>
                                    <textarea id="description" bind:value={newTicket.description} class="textarea textarea-bordered h-24" placeholder="Please provide as much detail as possible..." required></textarea>
                                </div>
                                <div class="form-control w-full">
                                    <label class="label" for="priority"><span class="label-text">Priority</span></label>
                                    <select id="priority" bind:value={newTicket.priority} class="select select-bordered">
                                        <option>Low</option>
                                        <option selected>Medium</option>
                                        <option>High</option>
                                    </select>
                                </div>
                                {#if ticketFormMessage}
                                    <div class="alert {ticketFormMessage.type === 'success' ? 'alert-success' : 'alert-error'} mt-4">
                                        <span>{ticketFormMessage.text}</span>
                                    </div>
                                {/if}
                                <div class="card-actions justify-end mt-6">
                                    <button type="submit" class="btn btn-primary" disabled={isSubmittingTicket}>
                                        {#if isSubmittingTicket}
                                            <span class="loading loading-spinner"></span>
                                        {/if}
                                        Submit Ticket
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <p class="text-2xl m-2 mt-12">Your open tickets</p>
                <!-- Non-admin List View -->
                <ul class="space-y-2">
                    {#each data.protectedData as ticket (ticket.ticketId)}
                        <li class="card bg-base-100 shadow-md hover:shadow-lg transition-shadow cursor-pointer" onclick={() => handleRowClick(ticket)}>
                            <div class="card-body p-4 flex-row justify-between items-center">
                                <div>
                                    <h3 class="card-title text-lg">{ticket.title}</h3>
                                    <p class="text-sm opacity-70">Priority: {ticket.priority} &bull; Status: {ticket.statusForCreator}</p>
                                </div>
                                <span class="text-xs opacity-50">{formatDate(ticket.createdAt)}</span>
                            </div>
                        </li>
                    {/each}
                </ul>
            {/if}
        {/if}
    {:else}
        <p>Please log in to access this page.</p>
    {/if}
</div>

<!-- Ticket Details Modal -->
<div class="modal {selectedTicket ? 'modal-open' : ''}">
    <div class="modal-box w-11/12 max-w-2xl">
        {#if selectedTicket}
            <h3 class="font-bold text-2xl">{selectedTicket.title}</h3>
            <p class="py-4 text-lg">{selectedTicket.description}</p>
            <div class="grid grid-cols-2 gap-4 mt-4 text-sm">
                <div><strong>Ticket ID:</strong> #{selectedTicket.ticketId}</div>
                <div><strong>Assigned to ID:</strong> #{selectedTicket.assignedToId}</div>
                <div><strong>Created:</strong> {formatDate(selectedTicket.createdAt)}</div>
                <div><strong>Priority:</strong> <span class="badge {getPriorityClass(selectedTicket.priority)} text-white font-semibold">{selectedTicket.priority}</span></div>
                <div><strong>Creator Status:</strong> <span class="badge {getStatusClass(selectedTicket.statusForCreator)} font-semibold">{selectedTicket.statusForCreator}</span></div>
                <div><strong>Assigned Status:</strong> <span class="badge {getStatusClass(selectedTicket.statusForAssigned)} font-semibold">{selectedTicket.statusForAssigned}</span></div>
            </div>
            <div class="modal-action">
                <button class="btn btn-primary" onclick={() => openChatModal(selectedTicket.ticketId)} disabled={isLoadingChat}>
                    {#if isLoadingChat}
                        <span class="loading loading-spinner"></span>
                    {/if}
                    View Chat
                </button>
                <button class="btn btn-error" onclick={() => handleDelete(selectedTicket.ticketId)}>Delete</button>
                <button class="btn" onclick={closeDetailsModal}>Close</button>
            </div>
        {/if}
    </div>
    <div class="modal-backdrop" onclick={closeDetailsModal}></div>
</div>

<!-- Chat History Modal -->
<div class="modal {isChatModalOpen ? 'modal-open' : ''}">
    <div class="modal-box w-11/12 max-w-2xl flex flex-col">
        <h3 class="font-bold text-xl mb-4">Chat History</h3>

        <div class="bg-base-200 p-4 rounded-lg h-96 overflow-y-auto flex flex-col space-y-4 mb-4">
            <!-- Chat messages display area -->
            {#if isLoadingChat}
                <div class="flex justify-center items-center h-full"><span class="loading loading-lg loading-spinner"></span></div>
            {:else if chatError}
                <div class="alert alert-error"><span>{chatError}</span></div>
            {:else if chatMessages.length > 0}
                {#each chatMessages as message (message.messageId)}
                    {@const isCurrentUser = data.user && message.userId === data.user.userId}
                    <div class="chat {isCurrentUser ? 'chat-end' : 'chat-start'}">
                        <div class="chat-header text-xs opacity-50 mb-1">User #{message.userId}</div>
                        <div class="chat-bubble {isCurrentUser ? 'chat-bubble-primary' : ''}">{message.text}</div>
                        <div class="chat-footer opacity-50 text-xs mt-1">{formatDate(message.createdAt)}</div>
                    </div>
                {/each}
            {:else}
                <div class="flex justify-center items-center h-full"><p>No chat messages found. Start the conversation!</p></div>
            {/if}
        </div>

        <!-- New Message Form -->
        <form class="flex gap-2" onsubmit={handleSendMessage}>
            <input type="text" placeholder="Type your message..." class="input input-bordered w-full" bind:value={newMessageText} disabled={isSendingMessage} />
            <button type="submit" class="btn btn-primary" disabled={isSendingMessage || !newMessageText.trim()}>
                {#if isSendingMessage}
                    <span class="loading loading-spinner"></span>
                {:else}
                    Send
                {/if}
            </button>
        </form>

        <div class="modal-action mt-2">
            <button class="btn btn-sm btn-circle absolute right-2 top-2" onclick={closeChatModal}>✕</button>
        </div>
    </div>
    <div class="modal-backdrop" onclick={closeChatModal}></div>
</div>
