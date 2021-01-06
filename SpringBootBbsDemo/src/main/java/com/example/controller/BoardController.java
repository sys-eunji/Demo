package com.example.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.service.BoardService;
import com.example.vo.BoardVO;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BoardController {
	@Autowired
	private BoardService boardService;

	@GetMapping("/list")
	public String list(Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		this.boardService.readAll(map);
		List<BoardVO> list = (List<BoardVO>)map.get("results");
		//log.warn("size = " + list.size());
		list.forEach(board -> {
			String filename = board.getFilename();
			if(filename != null) {
				int lastIndex = filename.lastIndexOf(".");
				String ext = filename.substring(lastIndex + 1);
				board.setFilename(ext);
			}
		});
		model.addAttribute("boardlist", list);
		return "list";     //  /templates/list.html
	}
	
	@GetMapping("/write")
	public String write() {
		return "write";     // /templates/write.html
	}
	
	@PostMapping("/write")
	public String write(BoardVO boardVO, 
								@RequestParam("company") String company,
								@RequestParam("file") MultipartFile file) throws IllegalStateException, IOException {
		String email = boardVO.getEmail();
		if(!email.equals("")) email += "@" + company;
		boardVO.setEmail(email);
//		log.warn("file = " + file.getOriginalFilename());
//		log.warn("email = " + boardVO.getEmail());
		this.boardService.create(boardVO, file);
		return "redirect:list";
	}
	
	@GetMapping("/view/{idx}")
	public String view(@PathVariable int idx, Model model) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("idx", idx);
		this.boardService.read(map);
		List<BoardVO> list = (List<BoardVO>)map.get("result");
		BoardVO boardVO = list.get(0);
		
		String name = boardVO.getName();
		name = rechange(name);
		boardVO.setName(name);
		
		String title = boardVO.getTitle();
		title = rechange(title);
		boardVO.setTitle(title);
		
		String contents = boardVO.getContents();
		contents = rechange(contents);
		boardVO.setContents(contents);
		
		String filename = boardVO.getFilename();
		if(filename != null) {
			int lastIndex = filename.lastIndexOf(".");
			String ext = filename.substring(lastIndex +1);
			model.addAttribute("ext", ext);
			
			lastIndex = filename.lastIndexOf("\\");
			String realFilename = filename.substring(lastIndex + 1);
			model.addAttribute("realFilename", realFilename);
		}
		
		model.addAttribute("board", boardVO);
		return "view";	// --> /template/view.html
	}
	private String rechange(String oldStr) {
		String newStr = oldStr.replace("''", "'"); 
		newStr = newStr.replace("&lt;", "<");
		newStr = newStr.replace("&gt;", ">");
		return newStr;
	}
	
	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE )
	public void download(@RequestParam("filename") String filename,
									HttpServletResponse response) {
		File file = new File(filename);
		int idx = filename.lastIndexOf("\\");
		String filename2 = filename.substring(idx + 1);
		
		try {
		filename2 = new String(filename2.getBytes("utf-8"), "ISO-8859-1");
		}catch(UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
		response.setHeader("Content-Disposition", "attachment; filename=" + filename2 + ";");
//		log.warn("경로 = " + file.getAbsolutePath());
//		log.warn("파일 존재 여부 = " + file.exists());
//		log.warn("파일 명 = " + filename2);
		try(InputStream is = new FileInputStream(file)){
			IOUtils.copy(is, response.getOutputStream());
		}catch(Exception e) {
			log.error(e.getMessage());
		}
	}
}
