package com.finance.entities.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PdfDTO {
private String month;
private String totalAvailable;
private String totalSpent;
private List<SummaryItDTO> listElement;
}
