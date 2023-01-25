package com.kos.CoCoCo.ja0.controller;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kos.CoCoCo.ja0.repository.BoardCategoryRepositoryH;
import com.kos.CoCoCo.ja0.repository.BoardRepositoryH;
import com.kos.CoCoCo.ja0.repository.ReplyRepositoryH;
import com.kos.CoCoCo.vo.BoardCategoryVO;
import com.kos.CoCoCo.vo.ReplyVO;
import com.kos.CoCoCo.vo.UserVO;

@RestController
@RequestMapping("/board")
public class BoardRestController {
	
	@Autowired
	ReplyRepositoryH rRepo;

	@Autowired
	BoardRepositoryH bRepo;
	
	@Autowired
	BoardCategoryRepositoryH bcRepo;
	
	@GetMapping("/findCategory/{categoryId}")
	public BoardCategoryVO findCategory(@PathVariable Long categoryId){
		return bcRepo.findByCategoryId(categoryId);
	}
	
	@GetMapping("/getReply/{boardId}")
	public List<ReplyVO> replyList(@PathVariable Long boardId){
		return rRepo.findByBoardId(boardId);
	}
	
	@PostMapping("/addReply/{boardId}")
	public List<ReplyVO> addReply(@PathVariable Long boardId, @RequestBody ReplyVO reply, HttpSession session){
		UserVO user = (UserVO) session.getAttribute("user");
		reply.setUser(user);
		reply.setBoard(bRepo.findById(boardId).get());

		rRepo.save(reply);
		
		return rRepo.findByBoardId(boardId);
	}

	@DeleteMapping("/deleteReply/{replyId}/{boardId}")
	public List<ReplyVO> deleteReply(@PathVariable Long replyId, @PathVariable Long boardId){
		
		rRepo.deleteById(replyId);
		
		return rRepo.findByBoardId(boardId);
	}
}
