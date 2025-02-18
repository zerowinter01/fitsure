package com.pretzero.fitsure.model.dao;

import java.util.List;

import com.pretzero.fitsure.model.dto.Notice;
import com.pretzero.fitsure.model.dto.SearchCondition;

public interface NoticeDao {

	void registNotice(Notice notice);
	
	int changeNotice(Notice notice);
	
	int deleteNotice(int noticeId);
	
	Notice selectNotice(int noticeId);
	
	List<Notice> allNoticeList();

	List<Notice> searchNotice(SearchCondition condition);
	
	void updateViewCnt(int noticeId);
}
