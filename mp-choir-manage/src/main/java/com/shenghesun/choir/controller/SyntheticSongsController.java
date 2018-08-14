package com.shenghesun.choir.controller;

import com.shenghesun.service.SyntheticSongsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("syn_songs")
public class SyntheticSongsController {

    @Autowired
    private SyntheticSongsService syntheticSongsService;

}
