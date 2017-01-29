# Basic prototype for Android Lite app with embeded makemytrip pwa application with network proxy.

FileProvider for serving static content from internal fileSystem. It helps serves static content like html's/css/js from local file system which helps avoiding serving from cdn.

Networkinterceptor is used to serve the static and dynamic content through StaticCache and DynamicCache resolver, and could work as a potential replacement for service worker for non supported devices.

DynamicCache could be configured for url's which uses okhttp3 file system cache to serve get requests, could be configured for posts too.

ServiceWorker toolbox is removed to serve the content from android file system, which gives control, fast accessibility and roll over policy for dynamic content without versions.