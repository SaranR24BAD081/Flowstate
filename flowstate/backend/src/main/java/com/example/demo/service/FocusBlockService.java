package com.example.demo.service;

import com.example.demo.dto.FocusBlockRequestDto;
import com.example.demo.dto.FocusBlockResponseDto;
import com.example.demo.entity.FlowUser;
import com.example.demo.entity.FocusBlock;
import com.example.demo.entity.FocusSession;
import com.example.demo.exception.BusinessValidationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.FlowUserRepository;
import com.example.demo.repository.FocusBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FocusBlockService {

    private final FocusBlockRepository blockRepository;
    private final FlowUserRepository userRepository;

    @Transactional
    public FocusBlockResponseDto registerBlock(FocusBlockRequestDto dto, Long practitionerId) {
        FlowUser practitioner = userRepository.findById(practitionerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!dto.getBlockEndTime().isAfter(dto.getBlockStartTime())) {
            throw new BusinessValidationException("Block end time must be after block start time");
        }
        if (!blockRepository.findOverlappingBlocks(practitionerId, dto.getDayOfWeek(),
                dto.getBlockStartTime(), dto.getBlockEndTime()).isEmpty()) {
            throw new BusinessValidationException("Focus block overlaps with an existing block on " + dto.getDayOfWeek());
        }
        FocusBlock block = new FocusBlock();
        block.setPractitioner(practitioner);
        block.setDayOfWeek(dto.getDayOfWeek());
        block.setBlockStartTime(dto.getBlockStartTime());
        block.setBlockEndTime(dto.getBlockEndTime());
        block.setIsProtected(false);
        if (dto.getPreferredSessionType() != null && !dto.getPreferredSessionType().isBlank()) {
            block.setPreferredSessionType(FocusSession.SessionType.valueOf(dto.getPreferredSessionType()));
        }
        return FocusBlockResponseDto.from(blockRepository.save(block));
    }

    @Transactional
    public FocusBlockResponseDto protectBlock(Long blockId, Long practitionerId) {
        return setProtected(blockId, practitionerId, true);
    }

    @Transactional
    public FocusBlockResponseDto unprotectBlock(Long blockId, Long practitionerId) {
        return setProtected(blockId, practitionerId, false);
    }

    /** Practitioners may delete only their own blocks; admins may delete any block. */
    @Transactional
    public void deleteBlock(Long blockId, Long userId, boolean isAdmin) {
        FocusBlock block = blockRepository.findById(blockId)
                .orElseThrow(() -> new ResourceNotFoundException("FocusBlock not found"));
        if (!isAdmin && !block.getPractitioner().getId().equals(userId)) {
            throw new ResourceNotFoundException("FocusBlock not found");
        }
        blockRepository.delete(block);
    }

    @Transactional(readOnly = true)
    public List<FocusBlockResponseDto> getMyBlocks(Long practitionerId) {
        return blockRepository.findByPractitionerIdOrderByDayOfWeekAscBlockStartTimeAsc(practitionerId).stream()
                .map(FocusBlockResponseDto::from).collect(Collectors.toList());
    }

    /** Weekly capacity plan: every registered block (coaches/admins see all practitioners). */
    @Transactional(readOnly = true)
    public List<FocusBlockResponseDto> getCapacityPlan() {
        return blockRepository.findAll().stream()
                .map(FocusBlockResponseDto::from).collect(Collectors.toList());
    }

    private FocusBlockResponseDto setProtected(Long blockId, Long practitionerId, boolean value) {
        FocusBlock block = blockRepository.findById(blockId)
                .orElseThrow(() -> new ResourceNotFoundException("FocusBlock not found"));
        if (!block.getPractitioner().getId().equals(practitionerId)) {
            throw new ResourceNotFoundException("FocusBlock not found");
        }
        block.setIsProtected(value);
        return FocusBlockResponseDto.from(blockRepository.save(block));
    }
}
