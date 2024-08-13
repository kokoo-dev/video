package com.kokoo.video.service

import com.kokoo.video.constant.FileConstant
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.lang.StringBuilder

@Service
class HlsService {

    fun createM3u8(file: MultipartFile) {
        val filename = FilenameUtils.getBaseName(file.originalFilename)
        val inputFile = File.createTempFile(filename, ".${FilenameUtils.getExtension(file.originalFilename)}")
        file.transferTo(inputFile)

        val outputFile = File("${FileConstant.DEFAULT_PATH}/$filename.m3u8")
        val command = createFfmpegCommand(inputFile.absolutePath, outputFile.absolutePath)
        Runtime.getRuntime().exec(command).waitFor()
    }

    fun createFfmpegCommand(
        inputFilePath: String,
        outputFilePath: String,
        hlsTime: Int = 10,
        hlsListSize: Int = 0
    ): String {
        return StringBuilder("ffmpeg -i ")
            .append(inputFilePath)
            .append(" -codec copy -hls_time ")
            .append(hlsTime)
            .append(" -hls_list_size ")
            .append(hlsListSize)
            .append(" ")
            .append(outputFilePath)
            .toString()
    }
}