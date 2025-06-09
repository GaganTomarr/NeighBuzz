package com.infy.lcp.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.lcp.dto.ModerationLogDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.ModerationLog;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ModerationLogRepo;

import jakarta.transaction.Transactional;

@Service(value="modLogService")
@Transactional
public class ModerationLogServiceImpl implements ModerationLogService {

	ModelMapper mapper=new ModelMapper();

	@Autowired
	private ModerationLogRepo modLogRepo;

	@Override
	public List<ModerationLogDTO> getAllModLogs() throws LCP_Exception {
		List<ModerationLog> modLogList=modLogRepo.findAll();
		if(modLogList.isEmpty()) {
			throw new LCP_Exception("service.MODERATION_LOG");
		}

		List<ModerationLogDTO> modLogDTOList= new ArrayList<>();
		modLogList.stream().forEach(obj->{
			ModerationLogDTO modLogDto=mapper.map(obj, ModerationLogDTO.class);
			modLogDto.setAdmin(mapper.map(obj.getAdmin(), UsersDTO.class));
			modLogDTOList.add(modLogDto);
		});

		return modLogDTOList;
	}
}