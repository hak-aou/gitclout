<script>
	import { onMount } from 'svelte';
    import Informations from './Informations.svelte';
    import Time, { svelteTime } from "svelte-time";

    let url = '';
    let repositories = [];
    let tagsByRepositoryId = {}; // We have tags for each repository id
    let allTagNotFinish = true;  // To know if we can show tags on screen

    // For next page
    let choosingTag = null;
    let choosingRepository = null;
    let contributorsByTagId = null;

    let newRepositories = [];

    async function getData(path) {
        const res = await fetch(path, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (res.ok) {
            return res.json();
        }
        return null;
    }


    async function sendUrl(url) {
        if(repositories != null && repositories.length > 0 && repositories.map(r => r.url).includes(url)) {
             alert("We already analyze the repository!");
             return;
        }

        console.log("Send url to the backend " + url);

        await fetch("http://localhost:8085/api/analyze/insertTags", {
            method: "POST",
            headers: {
                "Content-type": "application/json"
            },
            body: JSON.stringify({
                url: url
            })
        });

       await update();
    }

    async function getTagById(id) {
        let tags = await getData("http://localhost:8085/api/tags/repository/" + id);
        tagsByRepositoryId[id] = tags;
    }

    async function update() {
        allTagNotFinish = true;
        newRepositories = await getData("http://localhost:8085/api/repositories/all");

        if(newRepositories && newRepositories.length > 0) {
            for (const repository of newRepositories) {
                await getTagById(repository.repositoryId);
            }
            repositories = newRepositories;
            allTagNotFinish = false;
        }
    }

    async function onNext(tag, repository) {
        choosingTag = tag;
        choosingRepository = repository;

        let tagContributors = null;
        try {
            tagContributors = await getData("http://localhost:8085/api/contributors/tag/" + tag.tagId);
        } catch (error) {
            console.log("Error to get contributors of the tag :" + tag.name);
        }

        if (tagContributors == null) {
             await analyzeTag(tag.tagId);
             tagContributors = await getData("http://localhost:8085/api/contributors/tag/" + tag.tagId);
        }
        contributorsByTagId = tagContributors;
    }

    function onBack() {
        choosingTag = null;
        choosingRepository = null;
        contributorsByTagId = null;
    }

    async function analyzeTag(id) {
        await fetch("http://localhost:8085/api/analyze/tag", {
                    method: "POST",
                    headers: {
                        "Content-type": "application/json"
                    },
                    body: JSON.stringify({
                        tagId: id
                    })
                });
    }

    let promise = update();
    $: repositories = newRepositories;
</script>


<main>
	<h2>GitClout</h2>

    <!-- First page -->
    {#if choosingTag}
        <Informations  onNextFunction = {onNext}
                       onBackFunction = {onBack}
                       contributors = {contributorsByTagId}
                       tag = {choosingTag}
                       repository = {choosingRepository}
                       tagsByRepositoryId = {tagsByRepositoryId} />
    {:else}

    <!-- Second page -->
    <h5>Examples repository :</h5>
    <div class="examples">
        <p>https://github.com/openjdk/jdk.git</p>
        <p>https://gitlab.ow2.org/asm/asm.git</p>
        <p>https://github.com/juspay/hyperswitch</p>
        <p>https://github.com/Platane/snk</p>
        <p>https://github.com/ilyagru/Space-Snake</p>
    </div>

    <form on:submit|preventDefault={() => sendUrl(url.trim())}>
        <input type='url' bind:value={url} placeholder="https://..." />
	    <button disabled={!url} type='submit'> 📤 </button>
    </form>

    <hr>

    <div class="repositories">
        {#await promise}
          <p>Chargement...</p>
        {:then}
            {#each repositories as repository}
                <div class="repository">
                    <div class="name">
                        <a href="{repository.url}">{repository.url}</a>
                        <p>Repository added on :</p>
                        <Time timestamp="{new Date(repository.date)}" format="D/MM/YYYY"/>
                    </div>

                    <section class="tags_section">
                        <ol class="tags">
                          <p>Tags : </p>
                            {#if allTagNotFinish}
                                  <p>Chargement...</p>
                            {:else}
                                {#if tagsByRepositoryId[repository.repositoryId]}
                                    {#each tagsByRepositoryId[repository.repositoryId] as tag}
                                        <li class="tag"><button type=button on:click={() => onNext(tag, repository) } >{tag.name}</button></li>
                                    {/each}
                                {/if}
                            {/if}
                        </ol>
                    </section>
                    <hr>
                </div>
            {/each}
           {:catch error}
            <p>Nothing to show</p>
        {/await}
    </div>
    {/if}
</main>

<style>

	main {
        max-width: 40rem;
        margin: 0 15em;
        padding: 1rem;
	}

	h2 {
        text-align: center;
		color: #ff3e00;
		text-transform: uppercase;
		font-size: 4em;
		font-weight: 50;
	}

	@media (min-width: 640px) {
		main {
			max-width: none;
		}
	}


	p, button{
      display: inline-block;
    }

    .examples p {
      display: block;
    }


    ol li{
      display: inline;
    }

    .tags {
      margin: 0;
      margin-top: -1em;
      padding: 0;
    }

    ol {
      overflow-x: auto;
      white-space: nowrap;
    }

    ol li {
      display: inline;
    }
</style>