package com.infy.lcp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.lcp.dto.ForumDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.Forum;
import com.infy.lcp.entity.ForumCategory;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ForumRepo;
import com.infy.lcp.repository.UsersRepo;

import jakarta.transaction.Transactional;

@Service(value="forumService")
@Transactional
public class ForumServiceImpl implements ForumService {

	ModelMapper mapper=new ModelMapper();

	@Autowired
	ForumRepo forumRepository;

	@Autowired
	UsersRepo usersRepository;
	
	@Override
	public Integer addForum(ForumDTO forumDto) throws LCP_Exception {
		Optional<Users> userOpt=usersRepository.findById(forumDto.getUsers().getUserId());
		Users users=userOpt.orElseThrow(()-> new LCP_Exception("service.USER_NOT_EXIST"));

		

		Forum forum=mapper.map(forumDto, Forum.class);
		forum.setUser(mapper.map(forumDto.getUsers(), Users.class));
		forum.setCreatedAt(LocalDateTime.now());
		forum.setUpdatedAt(LocalDateTime.now());

		forumRepository.save(forum);
		
		return forum.getForumId();
	}

	@Override
	public void updateForum(Integer forumId, ForumDTO forumDto) throws LCP_Exception {
		Optional<Forum> forumOpt=forumRepository.findById(forumId);
		Forum forum=forumOpt.orElseThrow(()->new LCP_Exception("service.FORUM_EXIST"));

		if(forum.getUser().getUserId()!=forumDto.getUsers().getUserId()) {
			throw new LCP_Exception("service.FORUM_CREATER");
		}

		forum.setForumId(forumId);
		forum.setForumDescription(forumDto.getForumDescription());
		forum.setUpdatedAt(LocalDateTime.now());
	}

	@Override
	public void deleteForum(Integer forumId) throws LCP_Exception {
		Optional<Forum> forumOpt=forumRepository.findById(forumId);
		Forum forum=forumOpt.orElseThrow(()->new LCP_Exception("service.FORUM_EXIST"));

		if(forum.getUser()!=null) {
			forum.setUser(null);
		}

		forumRepository.deleteById(forumId);
	}

	@Override
	public List<ForumDTO> getAllForums() throws LCP_Exception {
		List<Forum> forumInList=forumRepository.findAll();
		if(forumInList.isEmpty()) {
			throw new LCP_Exception("service.FORUM_EXIST");
		}

		List<ForumDTO> forumOutList=new ArrayList<>();
		forumInList.stream().forEach(obj->{
			ForumDTO forumDto=mapper.map(obj, ForumDTO.class);
			forumDto.setUsers(mapper.map(obj.getUser(), UsersDTO.class));
			forumOutList.add(forumDto);
		});

		return forumOutList;
	}

	@Override
	public List<ForumDTO> getForumByCategory(ForumCategory forumCategory) throws LCP_Exception{
		List<Forum> forumList=forumRepository.findByCategory(forumCategory);
		if(forumList.isEmpty()) {
			throw new LCP_Exception("service.FORUM_NOT_FOUND");
		}
		List<ForumDTO> forumDTOList= new ArrayList<>();
		for(Forum forum: forumList) {
			ForumDTO forum1=mapper.map(forum,ForumDTO.class);
			forumDTOList.add(forum1);
		}
		return forumDTOList;
	}

}