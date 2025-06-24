package com.sba.applications.service;

import com.sba.applications.dto.ApplicationDTO;
import com.sba.applications.pojos.Application;

public interface ApplicationService {

   Application createApplication(ApplicationDTO application);

}
