package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import java.util.List;

public interface AcceptedStateService {

  List<AcceptedState> getAll();

  AcceptedState createNewAcceptedState(AcceptedState newAcceptedState);

  AcceptedState updateAcceptedStateDescription(String statusTag, String newDescription);

  void deleteUnusedAcceptedState(String statusTag);

}

