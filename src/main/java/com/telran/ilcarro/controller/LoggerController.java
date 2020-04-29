package com.telran.ilcarro.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping
@Slf4j
public class LoggerController {
    @Value("${fileName}")
    private String fileName;
    @Value("${password}")
    private String password;

    @ApiOperation(value = "Get loggers")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = String.class),
            @ApiResponse(code = 400, message = "Error! Bad token"),
            @ApiResponse(code = 401, message = "Error! Unauthorized"),
            @ApiResponse(code = 404, message = "Error! Not found")
    })
    @GetMapping(value = "logger")
    public List<String> getLogger(@RequestParam(value = "password") String password) throws IOException {
        if (Files.exists(Paths.get(fileName)) && password.equals(password)) {
            try {
                File f = new File(fileName);
                List<String> lines = FileUtils.readLines(f, "UTF-8");
                return lines;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Collections.EMPTY_LIST;
    }
}
