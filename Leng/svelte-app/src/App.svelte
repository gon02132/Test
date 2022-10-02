<script>
	let name = 'world!';
	let count = 0;
	$: doubledCount = count * 2;
	// Test alert with react function -> Print alert and change value
	$: if (doubledCount === 4) alert("doubled is 4 now!") 

	let m = { x: 0, y: 0};
</script>

<!-- Print text -->
<h1>Hello {name}!</h1>

<!-- Button with dynamic value in the button -->
<button on:click={() => count++}>
	{count === 0 ? "Please Click it" : "Count: " + count}
</button>

<!-- Reffer dynamic value -->
<p>
	docubledCount = {doubledCount}
</p>

<!-- if/else -->
{#if count === 0}
	<p> Please press button! </p>
{:else}
	<p> You clicked button! </p>
{/if}

<p>x: {m.x} y: {m.y}</p>
<div style="background-color: gray" on:mousemove={(event)=> { m.x = event.clientX; m.y = event.clientY;}}>
Hover mouse on this color!</div>
<style>
	div { width: 100%; height: 100%; }
</style>