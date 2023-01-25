package com.kos.CoCoCo.ja0.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kos.CoCoCo.ja0.VO.KakaoUser;
import com.kos.CoCoCo.ja0.repository.KakaoUserRepository;
import com.kos.CoCoCo.ja0.repository.UserRepositoryH;
import com.kos.CoCoCo.security.MemberService;
import com.kos.CoCoCo.vo.UserVO;

@Controller
@RequestMapping("/kakao")
public class KakaoController {
	
	@Autowired
	UserRepositoryH uRepo;
	
	@Autowired
	KakaoUserRepository kRepo;
	
	@Autowired
	MemberService mservice;
	
	@Value("31874169a929ca0733a07e5327c15791")
	String clientId;
	
	@Value("http://localhost:7777/kakao/loginTest")
	String redirectUri;
	
    @GetMapping(value="/login")
    public String kakaoConnect() {
    	String url = "https://kauth.kakao.com/oauth/authorize?client_id=" + clientId
    				+"&redirect_uri="+ redirectUri +"&response_type=code";

        return "redirect:" + url;
    }
    
    @PostMapping(value="/getEmail")
    public String getEmail(KakaoUser kakaoUser) {
    	UserVO user = join(kakaoUser);
    	
    	// 강제 로그인 처리
        login(user);
        
    	return "redirect:/CoCoCo";
    }
    
    @GetMapping(value="/loginTest", produces="application/json")
    public String kakaoLogin(@RequestParam("code") String code, HttpSession session, Model model)throws IOException {
    	// 1. "액세스 토큰" 요청
    	String accessToken = getAccessToken(code);
    	
    	// 2. JsonNode 가져오기 
    	JsonNode jsonNode = getJsonNode(accessToken);
    	
    	// 3. user 정보 가져오기
    	KakaoUser kakaoUser = getKakaoUser(jsonNode);
    	
    	if(kakaoUser.getEmail() == null) { 
    		model.addAttribute("kakaoUser", kakaoUser);
    		return "/auth/kakaoForm";
    	}
    	
        // 4. 가입정보 없으면 회원가입
        UserVO user = uRepo.findById(kakaoUser.getEmail()).orElse(null);
        if (user == null) user = join(kakaoUser);

        // 5. 강제 로그인 처리
        login(user);
        
    	return "redirect:/CoCoCo";
    	
    }
    
    private String getAccessToken(String code) {
        String RequestUrl = "https://kauth.kakao.com/oauth/token"; 
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();

        postParams.add(new BasicNameValuePair("grant_type", "authorization_code"));
        postParams.add(new BasicNameValuePair("client_id", clientId)); 
        postParams.add(new BasicNameValuePair("redirect_uri", redirectUri)); 
        postParams.add(new BasicNameValuePair("code", code)); 

        final HttpClient client = HttpClientBuilder.create().build();
        final HttpPost post = new HttpPost(RequestUrl);

        JsonNode returnNode = null;

        try {
            post.setEntity(new UrlEncodedFormEntity(postParams));

            final HttpResponse response = client.execute(post);
            response.getStatusLine().getStatusCode();

            // JSON 형태 반환값 처리
            ObjectMapper mapper = new ObjectMapper();

            returnNode = mapper.readTree(response.getEntity().getContent());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnNode.get("access_token").asText();
    }
    
    private JsonNode getJsonNode(String accessToken) throws JsonProcessingException{
    	// HTTP Header 생성
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Authorization", "Bearer " + accessToken);
    	headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
    	
    	// HTTP 요청 보내기
    	HttpEntity<MultiValueMap<String, String>> kakaoRequest = new HttpEntity<>(headers);
    	RestTemplate rt = new RestTemplate();
    	
    	// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
    	ResponseEntity<String> response = rt.exchange(
    			"https://kapi.kakao.com/v2/user/me",
    			HttpMethod.POST,
    			kakaoRequest,
    			String.class
    			);
    	
    	//사용자 정보가 json으로 오는데 바디에서 빼준다.
    	String responseBody = response.getBody();
    	
    	//필요한 부분을 빼기 위해 ObjectMapper을 사용
    	ObjectMapper objectMapper = new ObjectMapper();
    	JsonNode jsonNode = objectMapper.readTree(responseBody);
    	
    	return jsonNode;
    }
    
    private KakaoUser getKakaoUser(JsonNode jsonNode) {
        Long id = jsonNode.get("id").asLong();
        
        Optional<KakaoUser> kakaoUser = kRepo.findById(id);
        if(kakaoUser.isPresent()) return kakaoUser.get();
        
        String nickname = jsonNode.get("properties").get("nickname").asText();
        String img = jsonNode.get("properties").get("profile_image").asText();
        
        boolean hasEmail = jsonNode.get("kakao_account").has("email");
        
        // 이메일 동의했을 때만 정보 가져오도록
        String email = null;
        if(hasEmail) email = jsonNode.get("kakao_account").get("email").asText();
        
        KakaoUser user = new KakaoUser(id, nickname, email, img);
        
        return user;
    }
    
    private UserVO join(KakaoUser kakaoUser) {
    	// kakaoUser 저장
    	kRepo.save(kakaoUser);
    	
    	UserVO user = UserVO.builder()
    			.image(kakaoUser.getImg())
    			.name(kakaoUser.getName())
    			.pw(UUID.randomUUID().toString())
    			.userId(kakaoUser.getEmail())
    			.build();
    			
    	UserVO newUser = mservice.joinUser(user);
    	return newUser;
    }

    private void login(UserVO kakaoUser) {
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}