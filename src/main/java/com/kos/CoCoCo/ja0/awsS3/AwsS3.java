package com.kos.CoCoCo.ja0.awsS3;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AwsS3 {

    private final AmazonS3Client amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file, String dir) throws IOException {
        String key =  dir + UUID.randomUUID() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        
        byte[] bytes = IOUtils.toByteArray(file.getInputStream());
        metadata.setContentLength(bytes.length);

        ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(bytes);

        PutObjectRequest request = new PutObjectRequest(bucket, key, byteArrayIs, metadata);
        request.withCannedAcl(CannedAccessControlList.AuthenticatedRead); // 접근권한 체크
        
        amazonS3.putObject(request);
        
        return amazonS3.getUrl(bucket, key).toString();
    }

    public void delete(String targetFile){
    	if(targetFile == null) return;
    	
    	String key = targetFile.substring(54);
    	String encodeKey="";
    	
    	try {
			encodeKey = URLDecoder.decode(key, "utf-8"); //한글 변환
			amazonS3.deleteObject(bucket, encodeKey);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
    	amazonS3.deleteObject(bucket, encodeKey);
    }
    
    public void copy(String originalKey, String dir, String newKey){
    	String oKey = originalKey.substring(54);
    	String nKey = dir + newKey;
    	
    	String encodeKey="";
    	try {
    		encodeKey = URLDecoder.decode(oKey, "utf-8"); //한글 변환
    	} catch (UnsupportedEncodingException e) {
    		e.printStackTrace();
    	}
    	
    	amazonS3.copyObject(bucket, encodeKey, bucket, nKey);
    }
    
    public ResponseEntity<byte[]> download(String dir, String file) throws IOException {
    	String key = dir + file;
    	
        S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, key));
        S3ObjectInputStream objectInputStream = ((S3Object) o).getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);
 
        String fileName = URLEncoder.encode(file, "UTF-8").replaceAll("\\+", "%20");
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);
 
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }
}
