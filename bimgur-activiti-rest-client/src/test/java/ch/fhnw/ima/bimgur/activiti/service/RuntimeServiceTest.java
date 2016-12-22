package ch.fhnw.ima.bimgur.activiti.service;

import ch.fhnw.ima.bimgur.activiti.IntegrationTest;
import ch.fhnw.ima.bimgur.activiti.TestUtils;
import ch.fhnw.ima.bimgur.activiti.model.ProcessDefinitionId;
import ch.fhnw.ima.bimgur.activiti.model.ProcessInstance;
import ch.fhnw.ima.bimgur.activiti.model.StartProcessInstanceById;
import ch.fhnw.ima.bimgur.activiti.model.StartProcessInstanceByKey;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.Assert.assertNotEquals;

@IntegrationTest
public class RuntimeServiceTest {

    private static RuntimeService runtimeService;

    @BeforeAll
    static void beforeAll() {
        runtimeService = TestUtils.client().getRuntimeService();

        TestUtils.resetDeployments();
        TestUtils.loadAndDeployTestDeployments(
                Collections.singletonList("demo-japanese-numbers.bpmn20.xml"));

        TestUtils.processEngine().getRuntimeService()
                .startProcessInstanceByKey("bimgur-demo-japanese-numbers");
    }

    @Test
    void startProcessInstanceByKey() {
        String processDefinitionKey = "bimgur-demo-japanese-numbers";
        StartProcessInstanceByKey startData = new StartProcessInstanceByKey(processDefinitionKey);
        Observable<ProcessInstance> result = RuntimeServiceTest.runtimeService.startProcessInstance(startData);
        result
                .map(ProcessInstance::getProcessDefinitionUrl)
                .test()
                .assertValue(url -> url.contains(processDefinitionKey));
    }

    @Test
    void startProcessInstanceById() {
        org.activiti.engine.RepositoryService repositoryService = TestUtils.processEngine().getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().list().get(0);
        ProcessDefinitionId processDefinitionId = new ProcessDefinitionId(processDefinition.getId());
        StartProcessInstanceById startData = new StartProcessInstanceById(processDefinitionId);
        Observable<ProcessInstance> result = RuntimeServiceTest.runtimeService.startProcessInstance(startData);


        result
                .map(ProcessInstance::getProcessDefinitionUrl)
                .test()
                .assertValue(url -> url.contains(processDefinition.getKey()));
    }

    @Test
    void completeTask() {

        org.activiti.engine.task.Task task = TestUtils.processEngine().getTaskService().createTaskQuery().list().get(0);
        Observable<ResponseBody> testObserver = runtimeService
                .complete(
                        task.getId(),
                        new TaskAction());
        testObserver.subscribe(responseBody -> {
            org.activiti.engine.task.Task task1 = TestUtils.processEngine().getTaskService().createTaskQuery().list().get(0);
            assertNotEquals(task, task1);
        });

        runtimeService
                .complete(
                        TestUtils.processEngine().getTaskService().createTaskQuery().list().get(0).getId(),
                        new TaskAction())
                .test().assertComplete();

    }

}
