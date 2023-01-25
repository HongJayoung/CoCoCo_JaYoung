package com.kos.CoCoCo.ja0.VO;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter // 자동생성....setter는 직접구현함
@AllArgsConstructor
@Builder
@ToString
public class PageVO {
	private static final int DEFAULT_SIZE = 4;
	private static final int DEFAULT_MAX_SIZE = 50;

	int page;
	int size;

	public PageVO() {
		this.page = 1;
		this.size = DEFAULT_SIZE;
	}

	public void setPage(int page) {
		this.page = page < 0 ? 1 : page;
	}

	public void setSize(int size) {
		this.size = size < DEFAULT_SIZE || size > DEFAULT_MAX_SIZE ? DEFAULT_SIZE : size;
	}

	public Pageable makePaging(int direction, String... props) { //... 배열
		Sort.Direction dir = direction == 0 ? Direction.DESC : Direction.ASC;
		return PageRequest.of(this.page - 1, this.size, dir, props);
	}
}
