package com.sba.applications.service;

import com.sba.applications.dto.ApplicationDTO;
import com.sba.applications.pojos.Application;

import java.util.List;

public interface ApplicationService {

   Application createApplication(ApplicationDTO application);
   List<Application> getAllApplications();
   Application getApplicationById(String id);
   Application updateApplication(String id, ApplicationDTO application);
   void deleteApplication(String id);
}
