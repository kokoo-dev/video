package com.kokoo.video.config

import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FFmpegExecutorConfig {

    @Bean
    fun ffmpegExecutor(): FFmpegExecutor {
        val ffmpeg = FFmpeg("/opt/homebrew/bin/ffmpeg")
        val ffprobe = FFprobe("/opt/homebrew/bin/ffprobe")

        return FFmpegExecutor(ffmpeg, ffprobe)
    }
}