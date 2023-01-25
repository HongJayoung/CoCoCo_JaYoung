package com.kos.CoCoCo.chat.repository;

import java.util.Map;

import org.springframework.data.repository.CrudRepository;

import com.kos.CoCoCo.chat.dto.ChatRoomDTO;
import com.kos.CoCoCo.vo.TeamVO;

public interface RoomRepository extends CrudRepository<TeamVO, Long>{
   //findAll()
}
