package com.kokoo.video.service

import com.kokoo.video.constant.FileConstant
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
import java.lang.StringBuilder
import java.time.LocalTime

@Service
class HlsService {

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
        val command = StringBuilder("ffmpeg -i ")
            .append(inputFilePath)
            .append(" -codec copy -hls_time ")
            .append(hlsTime)
            .append(" -hls_list_size ")
            .append(hlsListSize)
            .append(" ")
            .append(outputFilePath)
            .toString()

        Runtime.getRuntime().exec(command).waitFor()
    }

    fun createThumbnail(inputFilePath: String, outputFilePath: String, thumbnailTime: LocalTime) {
        val command = StringBuilder("ffmpeg -i ")
            .append(inputFilePath)
            .append(" -ss ")
            .append(thumbnailTime)
            .append(" -vcodec png -vframes 1 ")
            .append(outputFilePath)
            .toString()

        Runtime.getRuntime().exec(command).waitFor()
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