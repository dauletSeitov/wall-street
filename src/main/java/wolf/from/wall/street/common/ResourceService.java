package wolf.from.wall.street.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wolf.from.wall.street.resource.Resource;
import wolf.from.wall.street.resource.ResourceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }
}
