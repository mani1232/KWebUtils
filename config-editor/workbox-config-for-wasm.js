module.exports = {
    globDirectory: "build/dist/wasmJs/productionExecutable/",
    globPatterns: [
        "**/*.{html,js,css,wasm,png,jpg,ico,json}"
    ],
    maximumFileSizeToCacheInBytes: 20 * 1024 * 1024,
    runtimeCaching: [{
        urlPattern: ({ request }) => request.destination === 'document' || request.destination === 'script' || request.destination === 'style' || request.destination === 'image',
        handler: "StaleWhileRevalidate",
    }],
    swDest: "build/dist/wasmJs/productionExecutable/serviceWorker.js",
};