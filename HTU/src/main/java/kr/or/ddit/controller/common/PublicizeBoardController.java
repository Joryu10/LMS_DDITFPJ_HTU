package kr.or.ddit.controller.common;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.ddit.service.common.PublicizeBoardService;
import kr.or.ddit.util.FileUtil;
import kr.or.ddit.vo.BoardVO;
import kr.or.ddit.vo.Criteria;
import kr.or.ddit.vo.NoticeBoardVO;
import kr.or.ddit.vo.PageVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping(value = "/common/board/publicize")
@Controller
public class PublicizeBoardController {

	@Autowired
	PublicizeBoardService publicizeBoardService;
	
	@Autowired
	FileUtil fileUtil;

	///common/board/publicize/list?pageNum=2&amount=10&type=&keyword=
	@GetMapping("/list")
	public String list(Model model, Criteria cri, BoardVO boardVO) {
		log.info("list->boardVO : " + boardVO);
		log.info("list->cri : " + cri);
		
		List<BoardVO> boardList = this.publicizeBoardService.list(cri);

		model.addAttribute("boardList", boardList);

		model.addAttribute("list", publicizeBoardService.getList(cri));
		
		log.info("publicizeBoardService.getTotal(cri) : " + publicizeBoardService.getTotal(cri));
		
		model.addAttribute("pageMaker", new PageVO(cri, publicizeBoardService.getTotal(cri)));
		
		return "board/publicize_board/list";

	}

	@GetMapping("/create")
	public String create() {
		return "board/publicize_board/create";
	}

	@PostMapping("/create")
	public String create(BoardVO boardVO) {
		// BoardVO(bdCd=null, userCd=1001, comdCd=null, bdTitle=1212, bdCon=sfd,
		// bdReg=null, bdUdt=null, bdCnt=0, userVO=null,
		// userNm=작성자, fileList=null, commonDetailVO=null,
		// uploadFiles=[org.springframew..], commentList=null)
		log.info("boardVO ==> {}", boardVO);
		int cnt = this.publicizeBoardService.create(boardVO);

		if (cnt > 0)
			return "redirect:/common/board/publicize/list?bdCd=" + boardVO.getBdCd();
		else
			return "redirect:/common/board/publicize/list";
	}

	@GetMapping("/detail")
	public String detail(String bdCd, Model model) {
		// 게시글 상세 정보 조회
		BoardVO boardVO = this.publicizeBoardService.detail(bdCd);

		// 모델에 추가
		model.addAttribute("boardVO", boardVO);

		// 상세 페이지로 이동
		return "board/publicize_board/detail";
	}

	@GetMapping("/update")
	public String update(String bdCd, Model model) {

		BoardVO boardVO = this.publicizeBoardService.detail(bdCd);

		model.addAttribute("boardVO", boardVO);

		return "board/publicize_board/update";

	}

	@PostMapping("/update")
	public String update(BoardVO boardVO) {

		log.info("boardVO ==> {}", boardVO);

		int result = this.publicizeBoardService.update(boardVO);

		if (result > 0)
			return "redirect:/common/board/publicize/detail?bdCd=" + boardVO.getBdCd();
		else
			return "redirect:/common/board/publicize/list";

	}

	@GetMapping("/count")
	@ResponseBody
	public int count(String bdCd) {

		int cnt = this.publicizeBoardService.count(bdCd);

		return cnt;
	}

	@ResponseBody
	@PostMapping("/delete")
	public Map<String, String> deletePost(@RequestBody Map<String, String> map) {
	    int result = this.publicizeBoardService.delete(map);
	    
	    Map<String, String> response = new HashMap<>();
	    response.put("result", (result > 0) ? "success" : "failure");
	    
	    return response;
	}
	
	/**
	 * ckeditor로 파일 첨부하기
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@PostMapping("/fileuploadCK")
	public Map<String,Object> image(MultipartHttpServletRequest request) throws Exception {
		
		return fileUtil.uploadCkEditor(request);
	}

}