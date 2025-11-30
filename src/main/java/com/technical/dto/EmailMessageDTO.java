package com.technical.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class EmailMessageDTO {
	private String content;
	private String subject;
	private String from;
	private String to;// comma , to seperate email
	private Map<String, byte[]> attachmentMap;
}
