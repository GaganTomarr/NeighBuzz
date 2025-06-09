package com.infy.lcp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.infy.lcp.dto.ModerationLogDTO;
import com.infy.lcp.dto.UsersDTO;
import com.infy.lcp.entity.ModerationLog;
import com.infy.lcp.entity.Users;
import com.infy.lcp.exception.LCP_Exception;
import com.infy.lcp.repository.ModerationLogRepo;
import com.infy.lcp.service.ModerationLogServiceImpl;

@SpringBootTest
public class ModerationLogTest {
	@Mock
    private ModerationLogRepo modLogRepo;

    @Mock
    private org.modelmapper.ModelMapper mapper;

    @InjectMocks
    private ModerationLogServiceImpl modLogService; 

    private ModerationLog modLog;
    private Users admin;

    @BeforeEach
    void setUp() {
        admin = new Users();
        admin.setUserId(1);

        modLog = new ModerationLog();
        modLog.setLogId(10);
        modLog.setAdmin(admin);
    }

    @Test
    void getAllModLogs_shouldReturnMappedDTOList() throws LCP_Exception {
        
        ModerationLogDTO mappedDto = new ModerationLogDTO();
        UsersDTO adminDto = new UsersDTO();

        when(modLogRepo.findAll()).thenReturn(List.of(modLog));
        when(mapper.map(modLog, ModerationLogDTO.class)).thenReturn(mappedDto);
        when(mapper.map(admin, UsersDTO.class)).thenReturn(adminDto);

        
        List<ModerationLogDTO> result = modLogService.getAllModLogs();

        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(mappedDto, result.get(0));
        verify(modLogRepo).findAll();
        verify(mapper).map(modLog, ModerationLogDTO.class);
        verify(mapper).map(admin, UsersDTO.class);
    }

    @Test
    void getAllModLogs_shouldThrowExceptionWhenListIsEmpty() {
        
        when(modLogRepo.findAll()).thenReturn(Collections.emptyList());

        
        LCP_Exception ex = assertThrows(LCP_Exception.class, () -> {
            modLogService.getAllModLogs();
        });

        assertEquals("service.MODERATION_LOG", ex.getMessage());
        verify(modLogRepo).findAll();
        verifyNoInteractions(mapper);
    }
}
