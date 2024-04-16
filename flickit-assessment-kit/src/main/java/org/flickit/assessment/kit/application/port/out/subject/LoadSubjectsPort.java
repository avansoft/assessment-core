package org.flickit.assessment.kit.application.port.out.subject;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.Subject;

import java.util.List;

public interface LoadSubjectsPort {

    /**
     * Loads subjects associated with a specific kit ID and kit's last version,
     * ordered by their index.
     *
     * @param kitId The ID of the kit for which subjects are to be loaded.
     * @return A list of subjects associated with the given kit ID, ordered by index.
     * @throws ResourceNotFoundException if the kit ID is not found.
     */
    List<Subject> loadByKitId(long kitId);
}
