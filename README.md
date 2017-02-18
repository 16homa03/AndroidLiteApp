# Basic prototype for Android Lite app(~700kb) with embeded pwa application in Web View and provisoned with android and okhttp components. (Removed service worker from pwa app and written custom wrapper with android and okio network library, which supports all devices above gingerbread).

The apk size is ~700kb and supports all phones above Gingerbread.

Networkinterceptor is used to serve the static and dynamic content through StaticCache and DynamicCache resolver, and could work as a potential replacement for service worker for non supported devices.

Service Worker:- 
A service worker is a script that your browser runs in the background, separate from a web page, opening the door to features that don't need a web page or user interaction. Today, they already include features like push notifications and background sync.

StaticContentCache replacing Sw-Precache:- 
FileProvider for serving static content from internal fileSystem. It helps serves static content like html's/css/js from local file system which helps avoiding serving from cdn
 1. Serving static content(html, css, js, inages) while bundling within the app in assets. 
 2. The content will be read from fileprovider and will be pushed to filesystem. (Copied webapp folder from server and it's ready to go).
 3. FileSystem will server the content if present otherwise will make an okhttp get call to fetch content and synchronosly push the content again to secured file system.
 4. Index Db replication is used in conjunction with Android Content Providers and Database helpers.
 
DynamicContentCache replacing Sw-toolbox:-
DynamicCache could be configured for url's which uses okhttp3 file system cache to serve get requests, could be configured for both get and post method type.
Serve the content from android file system, which gives control, fast accessibility and roll over policy for dynamic content without versions.

Benifits :- 
 1. No network call for any static content.
 2. Better caching strategy as opposed to sw-precache.
 3. TODO: Needs to add a versioning service which pushes md5 hash from server to identify and refetch the speific content.
 4. Saves development efforts and publishing to android app store.
 5. Supports gingerbread and above versions.
 6. Prototype for makemytrip has been done.
 7. The same progressive website will serve your purpose for android and M Site.

 
