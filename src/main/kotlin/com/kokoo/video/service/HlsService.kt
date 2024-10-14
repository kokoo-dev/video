package com.kokoo.video.service

import com.kokoo.video.constant.FileConstant
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.apache.commons.io.FilenameUtils
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalTime


@Service
class HlsService(
    private val fFmpegExecutor: FFmpegExecutor
) {

    fun createM3u8(file: MultipartFile, thumbnailTime: LocalTime) {
        val filename = FilenameUtils.getBaseName(file.originalFilename)
        val inputFile = File.createTempFile(filename, ".${FilenameUtils.getExtension(file.originalFilename)}")
        file.transferTo(inputFile)

        val outputM3u8File = File("${FileConstant.DEFAULT_PATH}$filename.m3u8")
        createM3u8AndTsFile(inputFile.absolutePath, outputM3u8File.absolutePath)

        val outputPngFile = File("${FileConstant.DEFAULT_PATH}$filename.png")
        createThumbnail(inputFile.absolutePath, outputPngFile.absolutePath, thumbnailTime)
    }

    fun createM3u8AndTsFile(
        inputFilePath: String,
        outputFilePath: String,
        hlsTime: Int = 10,
        hlsListSize: Int = 0
    ) {
        val builder = FFmpegBuilder()
            .setInput(inputFilePath)
            .addOutput(outputFilePath)
            .setFormat("hls")
            .addExtraArgs("-hls_time", "10")
            .addExtraArgs("-hls_list_size", "0")
            .done()

        fFmpegExecutor.createJob(builder).run()
    }

    fun createThumbnail(inputFilePath: String, outputFilePath: String, thumbnailTime: LocalTime) {
        val builder: FFmpegBuilder = FFmpegBuilder()
            .overrideOutputFiles(true)
            .setInput(inputFilePath)
            .addExtraArgs("-ss", thumbnailTime.toString())
            .addOutput(outputFilePath)
            .setFrames(1)
            .done()

        fFmpegExecutor.createJob(builder).run()
    }

    fun getM3u8(baseFilename: String): ResponseEntity<Resource> {
        return getHls("$baseFilename.m3u8", MediaType.parseMediaType("application/vnd.apple.mpegurl"))
    }

    fun getTs(baseFilename: String): ResponseEntity<Resource> {
        return getHls("$baseFilename.ts", MediaType.APPLICATION_OCTET_STREAM)
    }

    private fun getHls(filename: String, mediaType: MediaType): ResponseEntity<Resource> {
        return try {
            val resource = FileSystemResource("${FileConstant.DEFAULT_PATH}$filename")

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            }

            val headers = HttpHeaders()
            headers.contentType = mediaType
            headers.setContentDispositionFormData("attachment", filename)

            ResponseEntity<Resource>(resource, headers, HttpStatus.OK)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}