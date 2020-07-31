hls/output.m3u8: hls
	ffmpeg -re -f lavfi -i testsrc=size=640x480:rate=30 -pix_fmt yuv420p -profile:v high -c:v libx264 -sc_threshold 0 -an -g 120 -keyint_min 120 -hls_time 4 -hls_list_size 5 -hls_flags delete_segments $@

hls:
	mkdir -p $@

.PHONY: clean
clean:
	rm -rf hls/
