<script>
    import { enhance } from '$app/forms';
</script>

<div class="mx-auto w-full max-w-sm flex flex-col">
    <h1 class="text-xl text-black my-2">Login</h1>
    <form method="POST" use:enhance>
        <div class="my-2">
            <label class="input" for="username">
                <span class="label">username</span>
                <input class="input" type="text" id="username" name="username" required />
            </label>
        </div>
        <div class="my-2">
            <label class="input" for="password">
                <span class="label">password</span>
                <input class="input" type="password" id="password" name="password" required />
            </label>
        </div>
        <button class="btn justify-center text-center" type="submit">Log In</button>
    </form>
</div>
