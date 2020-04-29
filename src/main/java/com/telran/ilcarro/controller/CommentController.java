package com.telran.ilcarro.controller;

import com.telran.ilcarro.exception.*;
import com.telran.ilcarro.model.dto.comment.CommentDto;
import com.telran.ilcarro.model.dto.comment.CommentPostDto;
import com.telran.ilcarro.service.interfaces.CommentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping
public class CommentController {
    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @ApiOperation(value = "Add new comment by car ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK, comment added!"),
            @ApiResponse(code = 400, message = "Error! Wrong comment format"),
            @ApiResponse(code = 401, message = "Error! Wrong authorization"),
            @ApiResponse(code = 404, message = "Error! Not found")
    })

    //@Anatoly
    @PostMapping("/comment")
    public CommentDto addComment(@RequestParam String serial_number
            , @RequestHeader("Authorization") String token
            , @RequestBody() CommentPostDto content
            /*, @RequestParam(value = "rating", required = false) int rating*/) {
        try {
            return commentService.addComment(serial_number, token, content, 10);
        } catch (CarDetailsException | RegistrationModelException
                | TokenValidationException | NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (AuthorizationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (ActionDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    //@Anatoly
    @ApiOperation(value = "Get latest comments")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = CommentDto[].class, message = "OK"),
            @ApiResponse(code = 404, message = "Error! Latest comments not found")
    })
    @GetMapping(value = "/comments")
    public Iterable<CommentDto> getLatestComments() {
        try {
            return commentService.getLatestComments();
        } catch (NotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
