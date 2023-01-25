package com.kos.CoCoCo.sol.service;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kos.CoCoCo.ja0.awsS3.AwsS3;
import com.kos.CoCoCo.sol.repository.NoticeFileRepository;
import com.kos.CoCoCo.sol.repository.NoticeRepository;
import com.kos.CoCoCo.sol.vo.NoticeFile;
import com.kos.CoCoCo.vo.NoticeVO;

@Service
public class NoticeService {

	@Autowired
	private NoticeRepository noticeRepo;
	
	@Autowired
	private NoticeFileRepository noticeFRepo;
	
	@Autowired
	private AwsS3 awsS3;
		
	public void insert(NoticeVO notice, MultipartFile[] files, String fileIds) throws Exception {
        
		NoticeVO newNotice = noticeRepo.save(notice); 
		
		for(MultipartFile file:files) {
			
			if (!file.isEmpty()) {
				String uploadfile = awsS3.upload(file, "uploads/noticefiles/");
				
				System.out.println("filepath : " + uploadfile);
				
				NoticeFile noticefile = NoticeFile.builder()
						.filename(uploadfile)
						.originFname(file.getOriginalFilename())
						.notice(newNotice)
						.build();

				noticeFRepo.save(noticefile);
		    }
		}
		
		if(fileIds!=null) {
			
			String[] arr = fileIds.split(",");
			for(String id : arr) {
				noticeFRepo.deleteById(Long.valueOf(id));
			}
		}
	}

		
}

