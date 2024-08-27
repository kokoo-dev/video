package com.kokoo.video.controller

import com.kokoo.video.service.HlsService
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/hls")
class HlsController(
    private val hlsService: HlsService
) {

    @PostMapping
    fun toM3u8(@RequestPart("file") file: MultipartFile) {
        hlsService.createM3u8(file)
    }

    @GetMapping("/{baseFilename}.m3u8")
    fun getM3u8(@PathVariable baseFilename: String): ResponseEntity<Resource> {
        return hlsService.getM3u8(baseFilename)
    }

    @GetMapping("/{baseFilename}.ts")
    fun getTs(@PathVariable baseFilename: String): ResponseEntity<Resource> {
        return hlsService.getTs(baseFilename)
    }
}