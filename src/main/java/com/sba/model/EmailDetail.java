package com.sba.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.FileSystemResource;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetail {
    String recipient;
    private String msgBody;
    private String subject;
    private String buttonValue;
    private String link;
    private FileSystemResource attachment;
    private String name;
    private String template;
    private Map<String, Object> extra;

}
