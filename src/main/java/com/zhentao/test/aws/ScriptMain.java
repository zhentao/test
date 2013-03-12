package com.zhentao.test.aws;

import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.elasticmapreduce.model.ActionOnFailure;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsRequest;
import com.amazonaws.services.elasticmapreduce.model.DescribeJobFlowsResult;
import com.amazonaws.services.elasticmapreduce.model.HadoopJarStepConfig;
import com.amazonaws.services.elasticmapreduce.model.JobFlowExecutionState;
import com.amazonaws.services.elasticmapreduce.model.JobFlowInstancesConfig;
import com.amazonaws.services.elasticmapreduce.model.PlacementType;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowRequest;
import com.amazonaws.services.elasticmapreduce.model.RunJobFlowResult;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;

public class ScriptMain {
    public static void main(String[] args) throws InterruptedException {
        AWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJIFQL7G6SZ2642TQ",
                "fT+/7eg5Fp8B7c87fOwkPfOoByYfgAMBVgKIYxFH");
        AmazonElasticMapReduceClient client = new AmazonElasticMapReduceClient(awsCredentials);

        HadoopJarStepConfig installSqoopStep = new HadoopJarStepConfig().withJar(
                "s3://elasticmapreduce/libs/script-runner/script-runner.jar").withArgs(
                "s3://zli-emr-test/script/install_sqoop.sh");

        StepConfig installSqoopConfig = new StepConfig().withName("step 1 - install sqoop")
                .withActionOnFailure(ActionOnFailure.TERMINATE_JOB_FLOW)
                .withHadoopJarStep(installSqoopStep);

        HadoopJarStepConfig loadDataStep = new HadoopJarStepConfig().withJar(
                "s3://elasticmapreduce/libs/script-runner/script-runner.jar").withArgs(
                "s3://zli-emr-test/script/load_to_mysql.sh");

        StepConfig loadDataConfig = new StepConfig().withName("step 2 - load data to mysql")
                .withActionOnFailure(ActionOnFailure.TERMINATE_JOB_FLOW)
                .withHadoopJarStep(loadDataStep);

        PlacementType placementType = new PlacementType().withAvailabilityZone("us-east-1b");
        JobFlowInstancesConfig instances = new JobFlowInstancesConfig().withEc2KeyName("zli-emr")
                .withInstanceCount(1)
                .withPlacement(placementType)
                .withMasterInstanceType("m1.small")
                .withSlaveInstanceType("m1.small")//.withKeepJobFlowAliveWhenNoSteps(true)
                .withHadoopVersion("0.20.205");
        RunJobFlowRequest runJobFlowRequest = new RunJobFlowRequest().withName("load data to rds via sqoop")
                .withSteps(installSqoopConfig, loadDataConfig)
                .withLogUri("s3n://zli-emr-test/log/log-api")
                .withAmiVersion("2.1.1")
                .withInstances(instances);

        RunJobFlowResult runJobFlow = client.runJobFlow(runJobFlowRequest);
        String jobFlowId = runJobFlow.getJobFlowId();
        System.out.println(jobFlowId);

        JobFlowExecutionState jobFlowExecutionState;
        boolean flowFinished = false;

        do {
            TimeUnit.SECONDS.sleep(20);
            DescribeJobFlowsRequest describeJobFlowsRequest = new DescribeJobFlowsRequest().withJobFlowIds(jobFlowId);

            DescribeJobFlowsResult describeJobFlowsResult = client.describeJobFlows(describeJobFlowsRequest);
            jobFlowExecutionState = JobFlowExecutionState.fromValue(describeJobFlowsResult.getJobFlows().get(0)// .getSteps().get(0)
                    .getExecutionStatusDetail()
                    .getState());
            System.out.printf("job state %s%n", jobFlowExecutionState);
            flowFinished = isFlowFinished(jobFlowExecutionState);
        } while (!flowFinished);

    }

    private static boolean isFlowFinished(JobFlowExecutionState jobFlowExecutionState) {
        switch (jobFlowExecutionState) {
            case COMPLETED:
            case FAILED:
            case TERMINATED:
                return true;

            case RUNNING:
            case SHUTTING_DOWN:
            case STARTING:
            case WAITING:
            case BOOTSTRAPPING:
                return false;

            default:
                return false;
        }
    }
}
