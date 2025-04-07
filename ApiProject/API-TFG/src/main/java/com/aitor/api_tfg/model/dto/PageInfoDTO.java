package com.aitor.api_tfg.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDTO {
    private int count;
    private int pages;
    private String next;
    private String prev;
}

