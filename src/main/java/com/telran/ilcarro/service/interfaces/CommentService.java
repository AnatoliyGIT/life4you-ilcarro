package com.telran.ilcarro.service.interfaces;

import com.telran.ilcarro.model.dto.comment.CommentDto;
import com.telran.ilcarro.model.dto.comment.CommentPostDto;

public interface CommentService {

    CommentDto addComment(String serial_number, String token, CommentPostDto content, int rating);

    Iterable<CommentDto> getLatestComments();
}
