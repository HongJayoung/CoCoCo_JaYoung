package com.kos.CoCoCo.ja0.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kos.CoCoCo.ja0.VO.KakaoUser;
import com.kos.CoCoCo.vo.UserVO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetailsImpl implements UserDetails {

	private UserVO user;
    
	 @Builder
	 public UserDetailsImpl(UserVO kakaoUser) {
	        this.user = kakaoUser;
	 }

	    @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        Collection<GrantedAuthority> grantedAuthority = new ArrayList<>();
	        grantedAuthority.add(new GrantedAuthority() {
	            @Override
	            public String getAuthority() {
	                return "";
	            }
	        });

	        return grantedAuthority;
	    }

	    @Override
	    public String getUsername() {
	        return user.getUserId();
	    }

	    @Override
	    public String getPassword(){ 
	    	return user.getPw();
	    }

	    @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }

	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isEnabled() {
	        return true;
	    }
}
