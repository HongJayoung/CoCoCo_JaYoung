package com.kos.CoCoCo.ja0.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.ja0.VO.KakaoUser;

public interface KakaoUserRepository extends CrudRepository<KakaoUser, Long> {

	@Modifying
	@Query(value = "delete from kakao_user where email = ?1", nativeQuery = true)
	void deleteByEmail(String email);
}
