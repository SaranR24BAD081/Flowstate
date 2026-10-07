package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistractionBreakdownResponseDto {

    private long totalCount;
    private long totalSeconds;
    private List<CategoryStat> categories;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStat {
        private String category;
        private long count;
        private long totalSeconds;
    }
}
