hls/output.m3u8: hls
	ffmpeg -re -f lavfi -i testsrc=size=640x480:rate=30 -f lavfi -i anoisesrc=amplitude=0.1:color=pink:r=44100 -map 0:v -map 1:a -pix_fmt yuv420p -profile:v high -c:v libx264 -c:a aac -sc_threshold 0 -g 120 -keyint_min 120  -hls_time 4 -hls_list_size 5 -hls_flags delete_segments $@

hls:
	mkdir -p $@

.PHONY: clean
clean:
	rm -rf hls/
