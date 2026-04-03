package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import androidx.lifecycle.LiveData
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import java.util.concurrent.CompletableFuture

interface IssueTypeService {

  fun refresh(activity: Activity): CompletableFuture<List<IssueType>>

  fun getIssueTypes(): LiveData<List<IssueType>>

}

