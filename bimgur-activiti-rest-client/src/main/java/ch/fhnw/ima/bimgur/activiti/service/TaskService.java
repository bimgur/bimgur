package ch.fhnw.ima.bimgur.activiti.service;

import ch.fhnw.ima.bimgur.activiti.model.Task;
import ch.fhnw.ima.bimgur.activiti.model.TaskClaimDTO;
import ch.fhnw.ima.bimgur.activiti.model.TaskCompleteDTO;
import ch.fhnw.ima.bimgur.activiti.model.util.ResultList;
import ch.fhnw.ima.bimgur.activiti.service.util.ResultListExtractor;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.*;

public interface TaskService {
    /*
     - assignee
 - processInstanceId
 - candidateUser*/


    @GET("runtime/tasks")
    Observable<ResultList<Task>> getTasksResultList(@Query("assignee") String assigneeUserId);

    default Observable<Task> getTasks() {
        return ResultListExtractor.extract(getTasksResultList(null));
    }

    default Observable<Task> getFilteredTasks(@Query("assignee") String assigneeUserId) {
        return ResultListExtractor.extract(getTasksResultList(assigneeUserId));

    }

    /**
     * Called when the task is successfully executed.
     *
     * @param taskId          the id of the task to complete, cannot be null.
     * @param taskCompleteDTO the complete data transfer object, cannot be null.
     */
    @Headers("Content-type: application/json")
    @POST("runtime/tasks/{taskId}")
    Observable<ResponseBody> complete(@Path("taskId") String taskId, @Body TaskCompleteDTO taskCompleteDTO);

    /**
     * Claim responsibility for a task: the given user is made assignee for the task.
     * The difference with {@link # setAssignee(String, String)} is that here
     * a check is done if the task already has a user assigned to it.
     * No check is done whether the user is known by the identity component.
     *
     * @param taskId          task to claim, cannot be null.
     * @param taskCompleteDTO user that claims the task. When userId is null the task is unclaimed,
     *                        assigned to no one.
     */
    @Headers("Content-type: application/json")
    @POST("runtime/tasks/{taskId}")
    Observable<ResponseBody> claim(@Path("taskId") String taskId, @Body TaskClaimDTO taskCompleteDTO);

}