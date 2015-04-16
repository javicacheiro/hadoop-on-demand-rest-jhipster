package es.cesga.hadoop.service.impl;

import es.cesga.hadoop.service.CloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OpenNebulaService implements CloudService {

    private final Logger log = LoggerFactory.getLogger(OpenNebulaService.class);

}
