package com.sba.applications.mapper;

import com.sba.applications.dto.ApplicationDTO;
import com.sba.applications.pojos.Application;
public class ApplicationMapper {
    public static ApplicationDTO toDTO(Application entity) {
        if (entity == null) return null;
        ApplicationDTO dto = new ApplicationDTO();
        dto.setCampus(entity.getCampus().getName());
        dto.setMajor(entity.getMajor().getName());
        return dto;
    }
}

