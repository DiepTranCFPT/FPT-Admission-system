package com.sba.campuses.service;

import com.sba.campuses.dto.ChildMajorRequest;
import com.sba.campuses.dto.MajorRequest;
import com.sba.campuses.pojos.Campus;
import com.sba.campuses.pojos.Major;
import com.sba.campuses.pojos.Major_Campus;
import com.sba.campuses.repository.MajorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MajorServiceImp implements MajorService {

    @Autowired
    private MajorRepository majorRepository;

    @Override
    public List<Major> getAll() {
        return majorRepository.findAll();
    }

    @Override
    public Major save(MajorRequest majorRequest) {
        Major major = new Major();
        major.setName(majorRequest.getName());
        major.setDescription(majorRequest.getDescription());
        major.setDuration(majorRequest.getDuration());
        major.setFee(majorRequest.getFee());
        return majorRepository.save(major);
    }

    @Override
    public Major update(String id, MajorRequest majorRequest) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Major not found with id: " + id));
        major.setName(majorRequest.getName());
        major.setDescription(majorRequest.getDescription());
        major.setDuration(majorRequest.getDuration());
        major.setFee(majorRequest.getFee());
        return majorRepository.save(major);
    }

    @Override
    public void delete(String id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Major not found with id: " + id));

        // Check if major has child majors
        List<Major> childMajors = majorRepository.findByParentMajors(major);
        if (!childMajors.isEmpty()) {
            throw new RuntimeException("Cannot delete major that has child majors. Please delete all child majors first.");
        }

        // If major has a parent, remove the reference before deletion
        if (major.getParentMajors() != null) {
            major.setParentMajors(null);
            majorRepository.save(major);
        }

        // Now we can safely delete the major
        majorRepository.delete(major);
    }

    @Override
    public Major getbyId(String id) {
        return majorRepository.findById(id).get();
    }

    @Override
    public List<Major> getAllParentMajors() {
        return majorRepository.findByParentMajorsIsNull();
    }

    @Override
    public List<Major> getChildMajors(String majorId) {
        Major parentMajor = majorRepository.findById(majorId)
                .orElseThrow(() -> new RuntimeException("Parent major not found with id: " + majorId));
        return majorRepository.findByParentMajors(parentMajor);
    }

    @Override
    public Major saveChildMajor(ChildMajorRequest request) {
        // Find parent major
        Major parentMajor = majorRepository.findById(request.getParentMajorId())
                .orElseThrow(() -> new RuntimeException("Parent major not found"));

        // Create new child major
        Major childMajor = new Major();
        childMajor.setName(request.getName());
        childMajor.setDescription(request.getDescription());
        childMajor.setDuration(request.getDuration());
        childMajor.setFee(request.getFee());
        childMajor.setParentMajors(parentMajor);

        // Save child major
        return majorRepository.save(childMajor);
    }

    @Override
    public List<Major> getAllChildMajors() {
        List<Major> allMajors = majorRepository.findAll();
        return allMajors.stream()
                .filter(major -> major.getParentMajors() != null)
                .collect(Collectors.toList());
    }

    @Override
    public Major updateChildMajor(String id, ChildMajorRequest request) {
        Major childMajor = majorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Child major not found with id: " + id));

        // Verify this is actually a child major
        if (childMajor.getParentMajors() == null) {
            throw new RuntimeException("Cannot update: This is not a child major");
        }

        // If parent major is being changed
        if (!childMajor.getParentMajors().getId().equals(request.getParentMajorId())) {
            Major newParentMajor = majorRepository.findById(request.getParentMajorId())
                    .orElseThrow(() -> new RuntimeException("Parent major not found"));
            childMajor.setParentMajors(newParentMajor);
        }

        // Update other fields
        childMajor.setName(request.getName());
        childMajor.setDescription(request.getDescription());
        childMajor.setDuration(request.getDuration());
        childMajor.setFee(request.getFee());

        return majorRepository.save(childMajor);
    }

    @Override
    public List<Campus> getCampusesByMajor(Major major) {
        return major.getMajor_campuses().stream()
                .map(Major_Campus::getCampus)
                .collect(Collectors.toList());
    }

    @Override
    public List<Campus> getCampusesByMajorId(String majorId) {
        return majorRepository.findCampusesByMajorId(majorId);
    }

}
