# About

Testing if ExoPlayer can get PTS of HLS livestream
(if the player can see that it is currently playing 00:10:31 of the livestream, for example).

# HLS livestream

This will start generating livestream in hls/ directory:

```
make
```

Use static HTTP server to serve the stream:

```
http-server hls/
Starting up http-server, serving hls
Available on:
  http://127.0.0.1:8080
  http://192.168.0.134:8080
```

# Build and run the app

```
vim ./app/src/main/res/values/strings.xml

# And modify the following line to your local HTTP server's IP:
<string name="media_url_hls">http://192.168.0.134:8080/output.m3u8</string>
```

After changing `media_url_hls`, build and run the app on connected device, using Android Studio.