package com.example.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.example.vo.BoardVO;

public interface BoardService {
	void create(BoardVO boardVO, MultipartFile file) throws IllegalStateException, IOException;
	void read(Map map);
	void readAll(Map map);
	void update(BoardVO boardVo);
	void delete(int idx);
}
