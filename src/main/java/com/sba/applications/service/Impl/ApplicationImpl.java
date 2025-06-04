package com.sba.applications.service.Impl;

import com.sba.applications.dto.ApplicationDTO;
import com.sba.applications.pojos.Application;
import com.sba.model.ResponseObject;

public interface ApplicationImpl {

   Application createApplication(ApplicationDTO application);

}
