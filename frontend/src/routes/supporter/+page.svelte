<!--
  This file would be located at a path like `src/routes/reviews/+page.svelte`
  It displays the reviews for the logged-in support user.
-->
<script lang="ts">
    import type { PageData } from './$types';
    import { onMount } from 'svelte';
    import type {TicketRating} from "../api/support/[supporterId]/+server";

    export let data: PageData;

    // Local state for ratings, loading, and errors
    let ratings: TicketRating[] = [];
    let isLoading = true;
    let error: string | null = null;

    // Fetch ratings on the client-side when the component mounts
    onMount(async () => {
        // Ensure we have a logged-in support user before fetching
        if (data.user && data.user.role === 'Support') {
            try {
                // The user ID should come from the JWT payload (e.g., 'sub')
                const supportId = data.user.userId;

                // This endpoint should be created in your SvelteKit app,
                // which in turn calls your Java backend.
                const response = await fetch(`/api/support/${supportId}`);

                if (!response.ok) {
                    throw new Error('Failed to load your reviews.');
                }
                ratings = await response.json();
            } catch (e) {
                if (e instanceof Error) {
                    error = e.message;
                } else {
                    error = 'An unknown error occurred.';
                }
            } finally {
                isLoading = false;
            }
        } else {
            isLoading = false;
        }
    });


    // Reactive calculation for the average rating based on local state
    $: averageRating = calculateAverage(ratings);
    $: totalReviews = ratings?.length || 0;

    function calculateAverage(ratingsArray: TicketRating[] | undefined): number {
        if (!ratingsArray || ratingsArray.length === 0) {
            return 0;
        }
        const sum = ratingsArray.reduce((acc, curr) => acc + curr.rating, 0);
        return parseFloat((sum / ratingsArray.length).toFixed(2));
    }

    function formatDate(dateString: string): string {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
        });
    }

    // Helper component for rendering stars, included directly for simplicity
    function Star({ filled }: { filled: boolean }) {
        return `
      <svg class="w-5 h-5 ${filled ? 'text-orange-400' : 'text-gray-300'}" fill="currentColor" viewBox="0 0 20 20">
        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
      </svg>
    `;
    }
</script>

<div class="p-4 sm:p-6 lg:p-8 bg-base-200 min-h-screen">
    {#if data.user}
        <!-- This content is only shown to users with the 'Support' role -->
        {#if data.user.role === 'Support'}
            <div class="max-w-4xl mx-auto">
                <h1 class="text-4xl font-bold mb-2">Your Reviews</h1>
                <p class="text-lg text-base-content/70 mb-8">Here's what users are saying about your support.</p>

                <!-- Stats Section -->
                <div class="stats shadow bg-base-100 mb-8 w-full">
                    <div class="stat">
                        <div class="stat-figure text-secondary">
                            {@html Star({ filled: true })}
                        </div>
                        <div class="stat-title">Average Rating</div>
                        <div class="stat-value text-secondary">{averageRating} / 5.0</div>
                    </div>
                    <div class="stat">
                        <div class="stat-title">Total Reviews</div>
                        <div class="stat-value text-primary">{totalReviews}</div>
                    </div>
                </div>

                <!-- Reviews List -->
                <div class="space-y-4 mt-4">
                    {#if isLoading}
                        <div class="card bg-base-100 shadow-md">
                            <div class="card-body items-center text-center">
                                <span class="loading loading-lg loading-dots"></span>
                                <p>Loading reviews...</p>
                            </div>
                        </div>
                    {:else if error}
                        <div class="alert alert-error">
                            <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                            <span>Error: {error}</span>
                        </div>
                    {:else if ratings.length > 0}
                        {#each ratings as review (review.ratingId)}
                            <div class="card bg-base-100 shadow-md">
                                <div class="card-body">
                                    <!-- Star Rating Display -->
                                    <div class="flex items-center mb-2">
                                        {#each { length: 5 } as _, i}
                                            {@html Star({ filled: i < review.rating })}
                                        {/each}
                                        <span class="ml-2 text-sm font-semibold text-base-content/60">({review.rating}.0 / 5.0)</span>
                                    </div>

                                    <!-- Review Text -->
                                    <p class="text-base-content/90 mb-4">"{review.text}"</p>

                                    <!-- Metadata -->
                                    <div class="card-actions justify-end text-xs text-base-content/50">
                                        <span>From Ticket #{review.ticketId}</span>
                                        <span>&bull;</span>
                                        <span>On {formatDate(review.createdAt)}</span>
                                    </div>
                                </div>
                            </div>
                        {/each}
                    {:else}
                        <div class="card bg-base-100 shadow-md">
                            <div class="card-body items-center text-center">
                                <p class="text-lg">You don't have any reviews yet.</p>
                                <p class="text-sm opacity-60">Keep up the great work!</p>
                            </div>
                        </div>
                    {/if}
                </div>
            </div>
        {:else}
            <!-- Message for non-support users -->
            <p>This page is only accessible to support staff.</p>
        {/if}
    {:else}
        <p>Please log in to view this page.</p>
    {/if}
</div>
