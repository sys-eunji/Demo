package com.example.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.dao.BoardDao;
import com.example.vo.BoardVO;

import lombok.extern.slf4j.Slf4j;

@Service("boardService")
@Slf4j
public class BoardServiceImpl implements BoardService {
	@Autowired
	private BoardDao boardDao;
	
	@Override
	public void create(BoardVO boardVO, MultipartFile file) throws IllegalStateException, IOException {
		if(!file.getOriginalFilename().equals("")) {
			String filename = file.getOriginalFilename();
			String saveFolder = System.getProperty("user.dir") + "\\files";
			File fileFile = new File(saveFolder);
			if(!fileFile.exists()) { //-->폴더가 존재하지 않는다면
				fileFile.mkdir(); //폴더 생성
			}
			String filePath = saveFolder + "\\" + filename; 
			//-->D:/SpringBootHome/SpringBootBbsDemo/files/springboot.png
			file.transferTo(new File(filePath)); //실제 하드디스크에 저장
			//log.warn("저장경로 = " + filePath);
			boardVO.setFilename(filePath);
		}
		String name = boardVO.getName();
		name = change(name);
		boardVO.setName(name);
		
		String title = boardVO.getTitle();
		title = change(title);
		boardVO.setTitle(title);
		
		String contents = boardVO.getContents();
		contents = change(contents);
		boardVO.setContents(contents);
		this.boardDao.insertBoard(boardVO);
	}
	
	private String change(String oldStr) {
		String newStr = oldStr.replace("'", "''"); //홑따옴표를 홑홑따옴표로
		newStr = newStr.replace("<", "&lt;");
		newStr = newStr.replace(">", "&gt;");
		return newStr;
	}
	


	@Override
	public void read(Map map) {
		this.boardDao.selectBoard(map);
	}

	@Override
	public void readAll(Map map) {
		this.boardDao.selectAllBoard(map);
	}

	@Override
	public void update(BoardVO boardVo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int idx) {
		// TODO Auto-generated method stub
		
	}

	
}
