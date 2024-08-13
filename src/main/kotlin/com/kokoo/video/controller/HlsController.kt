package com.kokoo.video.controller

import com.kokoo.video.service.HlsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
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
}