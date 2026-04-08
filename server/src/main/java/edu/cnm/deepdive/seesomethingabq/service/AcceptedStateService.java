package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import java.util.List;

/**
 * Service providing CRUD-style operations for {@link AcceptedState} entities.
 */
public interface AcceptedStateService {

  /**
   * Returns all accepted states.
   *
   * @return list of accepted states.
   */
  List<AcceptedState> getAll();

  /**
   * Returns an accepted state by status tag.
   *
   * @param statusTag status tag.
   * @return accepted state.
   */
  AcceptedState getByStatusTag(String statusTag);

  /**
   * Creates a new accepted state.
   *
   * @param newAcceptedState accepted state to create.
   * @return created entity.
   */
  AcceptedState createNewAcceptedState(AcceptedState newAcceptedState);

  /**
   * Updates the description for an accepted state.
   *
   * @param statusTag status tag of the state to update.
   * @param newDescription new description text.
   * @return updated entity.
   */
  AcceptedState updateAcceptedStateDescription(String statusTag, String newDescription);

  /**
   * Deletes an accepted state if it is not currently referenced by any reports.
   *
   * @param statusTag status tag of the state to delete.
   */
  void deleteUnusedAcceptedState(String statusTag);

}

