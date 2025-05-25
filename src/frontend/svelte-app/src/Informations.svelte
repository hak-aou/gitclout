<script>
    import { onMount } from 'svelte';
    import Chart from 'chart.js/auto'
    import Time, { svelteTime } from "svelte-time";

    export let onBackFunction;
    export let onNextFunction;

    export let contributors;
    export let tag;
    export let repository;
    export let tagsByRepositoryId;

    let chartCanvas;
    let myChart;

    function createStackedBarChart() {
        if (myChart) {
            myChart.destroy();
        }

        const data = {
              labels: contributors.map(c => c.mail),
              datasets: [
                  {
                      label: 'Builds',
                      backgroundColor: "blue",
                      data: contributors.map(c => c.buildsLines),
                  },
                  {
                      label: 'Codes',
                      backgroundColor: "red",
                      data: contributors.map(c => c.codesLines),
                  },
                  {
                      label: 'Configs',
                      backgroundColor: "green",
                      data: contributors.map(c => c.configsLines),
                  },
                  {
                      label: 'Docs',
                      backgroundColor: "yellow",
                      data: contributors.map(c => c.docsLines),
                  },
                  {
                      label: 'Resources',
                      backgroundColor: "purple",
                      data: contributors.map(c => c.resourcesLines),
                  }
              ]
          };

        const config = {
            type: 'bar',
            data: data,
            options: {
                plugins: {
                    title: {
                        display: true,
                        text: 'Stacked Bar Chart for contribution per type of files'
                    },
                },
                scales: {
                    x: {
                        stacked: true,
                    },
                    y: {
                        stacked: true
                    }
                }
            }
        };

        let ctx = chartCanvas.getContext('2d');
        myChart = new Chart(ctx, config);
    }

    let chartCanvasRadars = [];
    let ctxRadars = [];
    let myChartRadars = [];

    function createDiv(i) {
        const divParent = document.getElementById("allCanvas");

        const div = document.createElement("div");
        div.setAttribute("class", "divCanvas");
        div.setAttribute("id", "divMyChart" + i);
        divParent.appendChild(div);

        const createdCanvas = document.createElement("canvas");
        createdCanvas.setAttribute("id", "myChart" + i);
        createdCanvas.style.height = "250px"; createdCanvas.style.width = "250px";
        document.getElementById("divMyChart" + i).appendChild(createdCanvas);

        return createdCanvas;
    }

    function createRadarChart(i) {
        const createdCanvas = createDiv(i);

        const mapContributorLabels = [
            { label: 'Builds',    line: contributors[i].buildsLines },
            { label: 'Codes',     line: contributors[i].codesLines },
            { label: 'Configs',   line: contributors[i].configsLines },
            { label: 'Docs',      line: contributors[i].docsLines },
            { label: 'Resources', line: contributors[i].resourcesLines }
        ];

        let sort = mapContributorLabels.sort(function (a, b) {
            return b.line - a.line;
        });

        const data = {
              labels: sort.map(l => l.label),
              datasets: [
                    {
                        label: contributors[i].name + " - " + contributors[i].mail,
                        data:  sort.map(l => l.line),
                        fill: true,
                        backgroundColor: 'rgba(54, 162, 235, 0.2)',
                        borderColor: 'rgb(54, 162, 235)',
                        pointBackgroundColor: 'rgb(54, 162, 235)',
                        pointBorderColor: '#fff',
                        pointHoverBackgroundColor: '#fff',
                        pointHoverBorderColor: 'rgb(54, 162, 235)'
                    }
              ]
          };

        const config = {
            type: 'radar',
            data: data,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                elements: {
                    line: {
                        borderWidth: 3
                    }
                }
            }
        };

        let ctx = createdCanvas.getContext('2d');
        myChartRadars[i] = new Chart(ctx, config);
    }

    $: if (contributors && contributors.length > 0) {
        createStackedBarChart();

        const divParent = document.getElementById("allCanvas");
        divParent.innerHTML = '';
        myChartRadars = [];

        for (let i = 0; i < contributors.length; i++) {
            createRadarChart(i);
        }
    }
</script>

<main>
    <h3>Informations of the tag : {tag.name}</h3>
    <button type=button on:click={onBackFunction}>Previous page</button>

    <!-- 1. Set of tags -->
    <div class="name">
        <a href="{repository.url}">{repository.url}</a>
        <br><br><br>
    </div>

    <!-- 2. Contribution of all collaborators (using the stick view) -->
    <section class="tags_section">
        <ol class="tags">
          <p>Tags : </p>
            {#if tagsByRepositoryId[repository.repositoryId]}
                {#each tagsByRepositoryId[repository.repositoryId] as tag}
                    <li class="tag"><button type=button on:click={() => onNextFunction(tag, repository) } >{tag.name}</button></li>
                {/each}
            {/if}
        </ol>
    </section>
    <hr>

    <!-- 3. Radar diagrams of each employee -->
    <div id="barChart">
        <canvas bind:this={chartCanvas} id="myChart"></canvas>
    </div>
    <div id="allCanvas"></div>

</main>


<style>
	main {
		text-align: left;
		padding: 1em;
		max-width: 240px;
		margin: 0 auto;
	}


	@media (min-width: 640px) {
		main {
			max-width: none;
		}
	}


	p, button{
      display: inline-block;
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

    #allCanvas {
        flex-wrap: wrap;
        display: flex;
        justify-content: center;
    }


    #barChart {
       width: 60%;
       height: 60%;
       margin-right: auto;
       margin-left: auto;
       display: block;
    }

</style>