package com.kos.CoCoCo.ja0.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.kos.CoCoCo.ja0.VO.BoardFile;
import com.kos.CoCoCo.ja0.awsS3.AwsS3;
import com.kos.CoCoCo.ja0.repository.BoardCategoryRepositoryH;
import com.kos.CoCoCo.ja0.repository.BoardFileRepositoryH;
import com.kos.CoCoCo.ja0.repository.BoardRepositoryH;
import com.kos.CoCoCo.ja0.repository.ReplyRepositoryH;
import com.kos.CoCoCo.ja0.repository.TeamRepository;
import com.kos.CoCoCo.vo.BoardCategoryVO;
import com.kos.CoCoCo.vo.BoardVO;
import com.kos.CoCoCo.vo.UserVO;

@Controller
@RequestMapping("/board")
public class BoardController {

	@Autowired
	TeamRepository tRepo;
	
	@Autowired
	BoardRepositoryH bRepo;
	
	@Autowired
	BoardFileRepositoryH bfRepo;
	
	@Autowired
	BoardCategoryRepositoryH bcRepo;

	@Autowired
	ReplyRepositoryH rRepo;
	
	@Autowired
	AwsS3 awsS3;
	
	@GetMapping("")
	public String boardMain(Long categoryId, HttpSession session, Model model) {
		Long teamId = (Long) session.getAttribute("teamId");
		session.setAttribute("categoryList", bcRepo.findByTeamId(teamId));
		
		if(categoryId == null) {
			model.addAttribute("boardList", bRepo.findByTeamId(teamId));
		} else {
			model.addAttribute("boardList", bRepo.findByCategoryId(categoryId));
			model.addAttribute("category", bcRepo.findByCategoryId(categoryId));
			model.addAttribute("categoryId", categoryId);
		}
		
		return "/board/boardMain";
	}
	
	@GetMapping("/{boardId}")
	public String boardDetail(@PathVariable Long boardId, Model model) {
		BoardVO board = bRepo.findById(boardId).get();
		model.addAttribute("board", board);
		model.addAttribute("bfList", bfRepo.findByBoard(board));
		
		return "/board/boardDetail";
	}
	
	@GetMapping("/addCategory/{categoryName}")
	public String addCategory(@PathVariable String categoryName, HttpSession session) {
		Long teamId = (Long) session.getAttribute("teamId");
		
		BoardCategoryVO category = BoardCategoryVO.builder().categoryName(categoryName).team(tRepo.findById(teamId).get()).build();
		BoardCategoryVO newCaregory = bcRepo.save(category);
		
		session.setAttribute("categoryList", bcRepo.findByTeamId(teamId));
		
		return "redirect:/board?categoryId="+newCaregory.getBoardCategoryId();
	}
	
	@GetMapping("/modifyCategory")
	public String modifyCategory(BoardCategoryVO category, HttpSession session) {
		Long teamId = (Long) session.getAttribute("teamId");
		
		BoardCategoryVO boardCategory = bcRepo.findByCategoryId(category.getBoardCategoryId());
		boardCategory.setCategoryName(category.getCategoryName());
		bcRepo.save(boardCategory);
		
		session.setAttribute("categoryList", bcRepo.findByTeamId(teamId));
		
		return "redirect:/board?categoryId=" + category.getBoardCategoryId();
	}
	
	@Transactional
	@GetMapping("/deleteCategory/{categoryId}")
	public String deleteCategory(@PathVariable Long categoryId, HttpSession session) {
		Long teamId = (Long) session.getAttribute("teamId");
		
		bcRepo.deleteByCategoryId(categoryId);
		
		session.setAttribute("categoryList", bcRepo.findByTeamId(teamId));
		
		return "redirect:/board";
	}
	
	@GetMapping("/insert/{categoryId}")
	public String insertBoard(@PathVariable Long categoryId, Model model) {
		model.addAttribute("category", bcRepo.findByCategoryId(categoryId));
		return "/board/insertBoard";
	}
	
	@PostMapping("/insert")
	public String newBoard(Long categoryId, BoardVO board, MultipartFile[] files, Model model, HttpSession session) throws IOException {
		UserVO user = (UserVO) session.getAttribute("user");
		board.setUser(user);
		board.setCategory(bcRepo.findByCategoryId(categoryId));
		
		BoardVO newBoard = bRepo.save(board);
		saveFiles(newBoard, files);
			
		return "redirect:/board/" + newBoard.getBoardId();
	}

	@GetMapping("/modify/{boardId}")
	public String modifyBoard(@PathVariable Long boardId, Model model) {
		BoardVO board = bRepo.findById(boardId).get();
		model.addAttribute("board", board);
		
		return "/board/modifyBoard";
	}
	
	@PostMapping("/modify")
	public String modify(BoardVO board, MultipartFile[] files) throws IOException {
		BoardVO b = bRepo.findById(board.getBoardId()).get();
		b.setBoardTitle(board.getBoardTitle());
		b.setBoardText(board.getBoardText());
		
		BoardVO updateBoard = bRepo.save(b);
		saveFiles(updateBoard, files);

		return "redirect:/board/" + board.getBoardId();
	}

//	@Transactional
//	@GetMapping("/delete/{boardId}")
//	public String deleteBoard(@PathVariable Long boardId) {
//		BoardVO board = bRepo.findById(boardId).get();
//		awsS3.delete(board.getBoardFile());
//		
//		rRepo.deleteByBoardId(boardId);
//		bRepo.deleteById(boardId);
//		
//		return "redirect:/board";
//	}
	
	private void saveFiles(BoardVO board, MultipartFile[] files) throws IOException {
		for(MultipartFile file:files) {
			if(!file.isEmpty()) {
				String fileName = awsS3.upload(file, "uploads/boardFile/");
				
				BoardFile bFile = BoardFile.builder()
						.filename(fileName)
						.originFname(file.getOriginalFilename())
						.board(board)
						.build();
				
				bfRepo.save(bFile);
			}
		}
	}
}
