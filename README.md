# NBA DNA — Player Similarity Engine

Give it any NBA player. It returns the five players who most resemble them statistically, weighted by what actually matters at that player's position.

**Live demo → [nba-api-project.onrender.com](https://nba-api-project.onrender.com)**
<img width="1710" height="1107" alt="Screenshot 2026-08-06 at 12 00 57 AM" src="https://github.com/user-attachments/assets/c223e104-e51d-46b8-a29e-ff1bac89f693" />

<img width="1710" height="1107" alt="Screenshot 2026-08-06 at 12 01 21 AM" src="https://github.com/user-attachments/assets/42031035-1302-4946-ae41-3efa43365169" />

> Hosted on Render's free tier, so the container sleeps when idle. The first request after a period of inactivity takes up to 30 seconds to wake it. Subsequent requests are fast.

`Java 21` · `Spring Boot 3.2` · `Vanilla JS PWA` · `Docker`
---

## What it does

Type a player's name. The app confirms which player it matched, lets you choose how the comparison should be weighted, then returns the five closest players from the 2024 NBA season across 15 per-game statistical categories.

There are two weighting modes:

- **Smart Weights** (default) — position-based presets. A point guard is compared mainly on assists, scoring, turnovers, and shooting efficiency; a center mainly on rebounds, blocks, and finishing.
- **Custom** — set your own weight for any of the 15 stats using sliders. Only the stats you weight above zero are used in the comparison.

The frontend is a three-step flow — search, configure weights, results — served as a single page directly from the Spring Boot backend, and installable as a PWA.

## Why I built it

Stephen Curry has always been my favorite basketball players and my favorite celebrity for a long time. As time has, and continues to, pass I wanted to create a program that can, statistically, find players who are similar to our favorite players. I also wanted to see whether a deliberately simple and fully explainable distance model could reproduce the comparisons fans make intuitively — and whether weighting stats by position would get closer to how people actually think about role than treating every category as equally important. It started as a terminal program driven by `Scanner` prompts, and turning it into a deployed, installable web app meant building the whole path: paginated third-party API, in-memory caching, REST layer, frontend, container, deploy.

## How it works

### 1. Data ingestion

`NBAApiClient` pulls from the public [nbaapi.com](https://api.server.nbaapi.com) `playertotals` endpoint using the JDK's built-in `java.net.http.HttpClient` — no third-party HTTP library, no API key, no authentication beyond an `Accept: application/json` header.

```
GET https://api.server.nbaapi.com/api/playertotals?season=2024&pageSize=100&page=N
```

The endpoint is paginated, so the client reads `pagination.pages` from the first response and walks every page, accumulating `Player` objects from the `data` array.

The endpoint returns **season totals**, so volume statistics are divided by games played to get per-game rates. Percentage statistics are already rates and are used as reported:

| Key | Source field | Conversion |
|---|---|---|
| `pts` | `points` | ÷ games |
| `ast` | `assists` | ÷ games |
| `reb` | `totalRb` | ÷ games |
| `stl` | `steals` | ÷ games |
| `blk` | `blocks` | ÷ games |
| `to` | `turnovers` | ÷ games |
| `pf` | `personalFouls` | ÷ games |
| `min` | `minutesPg*` | ÷ games |
| `fga` | `fieldAttempts` | ÷ games |
| `fta` | `ftAttempts` | ÷ games |
| `3ptATM` | `threeAttempts` | ÷ games |
| `fgPCT` | `fieldPercent` | as reported |
| `ftPCT` | `ftPercent` | as reported |
| `3ptPCT` | `threePercent` | as reported |
| `efgPCT` | `effectFgPercent` | as reported |

*Although the source field says minutesPg it still returns total minutes
### 2. Scoring

Similarity is a **weighted Manhattan (L1) distance** across the selected categories:

```
score(target, candidate) = Σ | target.stat − candidate.stat | × weight(stat)
```

A **lower score means more similar**. The target player is excluded from their own results, candidates are sorted ascending, and the top five are returned.

L1 rather than Euclidean is a deliberate choice. Squaring the differences would let a single large-scale category dominate the result — minutes per game ranges to about 38 while steals sit near 1, so a squared minutes gap would swamp everything else. Absolute differences keep each category's contribution proportional to its weight.

### 3. Position-based weights

When no custom weights are supplied, `SimilarityService` selects a preset based on the target player's listed position:

| Stat | PG | SG | SF | PF | C |
|---|---|---|---|---|---|
| `pts` | 5.0 | 6.0 | 4.0 | 4.0 | 4.0 |
| `ast` | **6.0** | 4.0 | 2.0 | 1.5 | **1.0** |
| `reb` | **1.0** | 2.0 | 3.0 | 4.0 | **4.5** |
| `stl` | 2.0 | 2.0 | 3.0 | 2.0 | 1.5 |
| `blk` | **0.8** | 1.2 | 3.0 | 3.5 | **4.0** |
| `3ptATM` | 3.5 | 4.5 | 3.0 | 2.2 | 2.0 |
| `3ptPCT` | 3.5 | 4.5 | 3.0 | 2.2 | 2.0 |
| `fga` | 3.0 | 3.5 | 3.0 | 3.0 | 3.0 |
| `fgPCT` | 3.0 | 3.5 | 3.0 | 3.5 | 4.0 |
| `fta` | 1.5 | 2.0 | 1.5 | 2.5 | 2.5 |
| `ftPCT` | 2.0 | 2.5 | 2.0 | 2.5 | 3.0 |
| `efgPCT` | 5.0 | 5.5 | 4.0 | 5.0 | 5.0 |
| `to` | **4.5** | 2.0 | 2.0 | 2.0 | 2.0 |
| `min` | 5.0 | 5.0 | 5.0 | 5.0 | 5.0 |
| `pf` | 1.0 | 1.0 | 1.0 | 1.0 | 1.0 |

The gradients are the point: assists fall from 6.0 at point guard to 1.0 at center while rebounds and blocks climb in the opposite direction, so guards are graded against guard skills and bigs against big skills. Turnovers are weighted highest for point guards, where ball security is part of the job description.

### 4. Caching

`DataCache` is a Spring `@Component` that fetches the full player list **once, in its constructor**, at application startup. Every request afterward reads from that in-memory snapshot, so no request triggers an outbound API call. This is why startup logs `Loading players...` followed by a count, and it's part of why the free-tier cold start is slow.

## API

Two endpoints, both `GET`, both CORS-open (`@CrossOrigin(origins = "*")`).

### `GET /player`

Look up a single player.

| Parameter | Required | Description |
|---|---|---|
| `name` | yes | Case-insensitive substring match against the player's full name |

```bash
curl "http://localhost:8080/player?name=Jokic"
```

Returns a single player object with `id`, `fullName`, `position`, and a `stats` map. Returns an empty body if no player matches.

### `GET /similar`

Return the five most similar players.

| Parameter | Required | Description |
|---|---|---|
| `name` | yes | Target player, matched the same way as `/player` |
| `pts` `ast` `reb` `stl` `blk` `to` `min` `pf` `fga` `fta` `fgPCT` `ftPCT` `efgPCT` `threePtATM` `threePtPCT` | no | Custom weight for that category |

If **no** weight parameters are supplied, the position presets above are used. If **any** are supplied, only those categories are compared, with the weights given.

Note that two weight parameters are spelled differently from their internal keys, because a Java identifier can't begin with a digit: `threePtATM` maps to `3ptATM` and `threePtPCT` maps to `3ptPCT`.

```bash
# Position-based presets
curl "http://localhost:8080/similar?name=Jokic"

# Custom: compare only on rebounds, blocks, and scoring
curl "http://localhost:8080/similar?name=Jokic&reb=4.5&blk=4&pts=4"
```

Returns a JSON array of five player objects, closest first. Returns `[]` if the target name matches nobody.

## Running locally

The application lives in the `player-compare/` directory — all commands below are run from there.

**Prerequisites**

- JDK 21 or later
- Maven 3.9+ (no Maven wrapper is committed, so use a system install)
- Internet access at startup, since the player cache loads on boot

**Build and run**

```bash
cd player-compare
mvn clean package
java -jar target/player-compare-1.0-SNAPSHOT.jar
```

Then open <http://localhost:8080>. The frontend calls the API on the same origin, so no configuration is needed.

**With Docker**

```bash
cd player-compare
docker build -t nba-dna .
docker run -p 8080:8080 nba-dna
```

The Dockerfile is a two-stage build: `maven:3.9.6-eclipse-temurin-21` compiles the jar, then it's copied into a slim `eclipse-temurin:21-jre` runtime image.

## Project structure

```
player-compare/
├── Dockerfile                  Multi-stage build (Maven 21 → JRE 21)
├── pom.xml                     Spring Boot 3.2.0, Java 21
├── system.properties           Pins java.runtime.version=21 for the host
└── src/main/
    ├── java/com/erdgy/nba/
    │   ├── Main.java                     @SpringBootApplication entry point
    │   ├── DataCache.java                Loads all players once at startup
    │   ├── api/NBAApiClient.java         Paginated nbaapi.com client
    │   ├── model/Player.java             Identity + 15-stat map, totals → per-game
    │   ├── controller/PlayerController.java   /player and /similar
    │   └── service/
    │       ├── PlayerService.java        Name lookup
    │       ├── PlayerSimilarity.java     (player, score) pair
    │       └── SimilarityService.java    Weighted L1 scoring, position presets
    └── resources/static/
        ├── index.html           Single-page frontend, vanilla JS
        ├── manifest.json        PWA manifest
        ├── sw.js               Service worker
        └── icon-192.png, icon-512.png
```

## Progressive Web App

The client is installable and its shell works offline.

- `manifest.json` declares standalone display, portrait orientation, a `#08080f` background with a `#f97316` theme color, and maskable 192px and 512px icons.
- `sw.js` precaches the app shell — `/`, `index.html`, `manifest.json`, and both icons — under cache key `nba-dna-v1`, calling `skipWaiting()` on install and `clients.claim()` on activate so an updated worker takes over immediately, and deleting stale caches from prior versions.
- Fetch strategy is split by route. Requests to `/similar` and `/player` are **network-first** and never cached, since stale similarity results would be misleading; on failure they resolve to a JSON error body rather than throwing. Everything else is **cache-first** with a network fallback.
- `index.html` registers the worker and surfaces a custom install banner.

## Deployment

Deployed on Render as a Docker web service. `system.properties` pins `java.runtime.version=21` for buildpack-based hosts. The container exposes port 8080 and the entrypoint is `java -jar app.jar`.

## Known limitations

Being explicit about these, since they're design boundaries rather than bugs:

- **One season.** The 2024 season is hardcoded in the API request. Cross-season and career comparisons aren't supported.
- **Stats aren't normalized.** Raw per-game values feed straight into the distance calculation, so the weights are doing double duty — expressing importance *and* correcting for the fact that minutes and steals live on very different scales. Z-score normalization would separate those two concerns.
- **Name matching is a substring search.** `PlayerService.findPlayerByName` lowercases both sides and takes the first `contains` hit, so an ambiguous query returns whichever match the API happened to return first. The frontend works around this by asking you to confirm the matched player before continuing.
- **Data is a boot-time snapshot.** There's no refresh mechanism, and if nbaapi.com is unreachable at startup the cache constructor fails and the application won't start.
- **No automated tests yet.** Correctness has been checked by hand against known player comparisons.
- **Cold starts.** A consequence of free-tier hosting plus loading the full dataset at boot.

## Ideas for the next version

- Z-score normalization so weights express importance only
- Multi-season and career-average comparison
- Scheduled cache refresh instead of a boot-time-only load
- Unit tests for the scoring function and the totals-to-per-game conversion
- Exact-match name lookup with a disambiguation list instead of first-substring-wins
- Show the per-category contribution to each similarity score, so a result explains itself
