#!/bin/bash
HADOOP_INPUT=hdfs:///user/hadoop/cascading
hadoop distcp s3://zli-emr-test/output/cascading $HADOOP_INPUT

~/sqoop-1.4.1-incubating__hadoop-0.20-citygrid/bin/sqoop export --connect jdbc:mysql://yourserver.us-east-1.rds.amazonaws.com:3306/test --username myusername --password mypassword --table word_count --export-dir $HADOOP_INPUT --input-fields-terminated-by='\t' --update-mode insertonly