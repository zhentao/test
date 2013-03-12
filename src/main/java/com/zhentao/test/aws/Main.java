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

public class Main {
    public static void main(String[] args) throws InterruptedException {
        AWSCredentials awsCredentials = new BasicAWSCredentials("key",
                "secret");
        AmazonElasticMapReduceClient client = new AmazonElasticMapReduceClient(awsCredentials);

        HadoopJarStepConfig hadoopJarStep = new HadoopJarStepConfig().withMainClass("com.zhentao.test.CascadingMain")
                .withJar("s3n://zli-emr-test/test-0.0.1-SNAPSHOT.jar")
                .withArgs("s3n://zli-emr-test/input/cascading", "s3n://zli-emr-test/output/cascading");

        StepConfig customJarConfig = new StepConfig().withHadoopJarStep(hadoopJarStep)
                .withActionOnFailure(ActionOnFailure.TERMINATE_JOB_FLOW)
                .withName("step 1 - custom jar api");

        HadoopJarStepConfig steamingStep = new HadoopJarStepConfig().withJar(
                "/home/hadoop/contrib/streaming/hadoop-streaming.jar").withArgs("-input",
                "s3n://zli-emr-test/output/cascading", "-output", "s3n://zli-emr-test/output/streaming", "-mapper",
                "s3n://elasticmapreduce/samples/wordcount/wordSplitter.py", "-reducer", "aggregate");

        StepConfig streamingConfig = new StepConfig().withName("step 2 - streaming")
                .withActionOnFailure("TERMINATE_JOB_FLOW")
                .withHadoopJarStep(steamingStep);

        PlacementType placementType = new PlacementType().withAvailabilityZone("us-east-1b");
        JobFlowInstancesConfig instances = new JobFlowInstancesConfig().withEc2KeyName("zli-emr")
                .withInstanceCount(1)
                .withPlacement(placementType)
                .withMasterInstanceType("m1.small").withKeepJobFlowAliveWhenNoSteps(true)
                .withSlaveInstanceType("m1.small")
                .withHadoopVersion("0.20.205");
        RunJobFlowRequest runJobFlowRequest = new RunJobFlowRequest().withName("custom jar via java api")
                .withSteps(customJarConfig, streamingConfig)
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
