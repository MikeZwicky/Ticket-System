<script lang="ts">
	import '../app.css';
    import type {PageData} from "./$types";

	let { children, data } = $props();
</script>
<div class="navbar bg-base-100 shadow-lg">
    <div class="navbar-start">
        <a href="/" class="btn btn-ghost text-xl">Ticket-System</a>
    </div>

    <div class="navbar-end">
        <div class="hidden md:flex">
            <ul class="menu menu-horizontal px-1">
                {#if data.user}
                    <li><a href="/dashboard">Dashboard</a></li>
                    <li><a href="/supporter">Reviews</a></li>
                    <li><a href="/about">About</a></li>
                    <li><form method="POST" action="/logout"><button type="submit">Logout</button></form></li>
                {:else}
                    <li><a href="/login">Login</a></li>
                {/if}
            </ul>
        </div>

        <div class="dropdown dropdown-end md:hidden">
            <div role="button" class="btn btn-ghost">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
            </div>
            <ul class="menu menu-sm dropdown-content mt-3 z-[1] p-2 shadow bg-base-100 rounded-box w-52">
                <li><a href="/dashboard">Dashboard</a></li>
                <li><a href="/about">About</a></li>
            </ul>
        </div>
    </div>
</div>

    {@render children()}


