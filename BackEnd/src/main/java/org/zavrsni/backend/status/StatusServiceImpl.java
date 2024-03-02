package org.zavrsni.backend.status;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService{

    private final StatusRepository statusRepository;

    @Override
    public void createStatus(String status) {
        Status status1 = new Status();
        status1.setStatusType(status);
        statusRepository.save(status1);
    }
}
