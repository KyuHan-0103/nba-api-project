const CACHE_NAME = 'nba-dna-v1';

// Files that make up the app shell — cached on install
const APP_SHELL = [
  '/',
  '/index.html',
  '/manifest.json',
  '/icon-192.png',
  '/icon-512.png', 
];

// Install: cache the app shell so the UI loads even offline
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => cache.addAll(APP_SHELL))
      .then(() => self.skipWaiting())
  );
});

// Activate: clean up old caches from previous versions
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys =>
      Promise.all(keys
        .filter(key => key !== CACHE_NAME)
        .map(key => caches.delete(key))
      )
    ).then(() => self.clients.claim())
  );
});

// Fetch: serve from cache when possible, fall back to network
self.addEventListener('fetch', event => {
  const url = new URL(event.request.url);

  // Always go to network for API calls — live data should never be cached
  if (url.pathname.startsWith('/similar') || url.pathname.startsWith('/player')) {
    event.respondWith(
      fetch(event.request).catch(() =>
        new Response(JSON.stringify({ error: 'API unavailable offline' }), {
          headers: { 'Content-Type': 'application/json' }
        })
      )
    );
    return;
  }

  // For everything else: try cache first, fall back to network
  event.respondWith(
    caches.match(event.request).then(cached => cached || fetch(event.request))
  );
});
