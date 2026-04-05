package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import java.util.concurrent.CompletableFuture

interface IssueReportService {

  fun submit(activity: Activity, request: IssueReportRequest): CompletableFuture<Void?>

}
