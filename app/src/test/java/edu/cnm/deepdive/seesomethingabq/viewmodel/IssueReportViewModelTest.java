package edu.cnm.deepdive.seesomethingabq.viewmodel;

import static org.junit.jupiter.api.Assertions.assertNull;

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class IssueReportViewModelTest {

  @Test
  void resetStateClearsSubmittedAndThrowable() throws Exception {
    IssueReportViewModel viewModel = new IssueReportViewModel(new NoopIssueReportService());
    Field submittedField = IssueReportViewModel.class.getDeclaredField("submitted");
    Field throwableField = IssueReportViewModel.class.getDeclaredField("throwable");
    submittedField.setAccessible(true);
    throwableField.setAccessible(true);
    Object submitted = submittedField.get(viewModel);
    Object throwable = throwableField.get(viewModel);
    submitted.getClass().getMethod("setValue", Object.class).invoke(submitted, Boolean.TRUE);
    Throwable expected = new IllegalStateException("boom");
    throwable.getClass().getMethod("setValue", Object.class).invoke(throwable, expected);

    viewModel.resetState();

    Object submittedValue = submitted.getClass().getMethod("getValue").invoke(submitted);
    Object throwableValue = throwable.getClass().getMethod("getValue").invoke(throwable);
    assertNull(submittedValue);
    assertNull(throwableValue);
  }

  private static final class NoopIssueReportService implements IssueReportService {

    @Override
    public CompletableFuture<Void> submit(android.app.Activity activity, IssueReportRequest request) {
      return CompletableFuture.completedFuture(null);
    }

  }
}
