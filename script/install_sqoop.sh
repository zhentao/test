#!/bin/bash
BUCKET_NAME=zli-emr-test

cd ~

##Install sqoop on emr
SQOOP_FOLDER=sqoop-1.4.1-incubating__hadoop-0.20-citygrid
hadoop fs -copyToLocal s3://$BUCKET_NAME/$SQOOP_FOLDER.tar.gz $SQOOP_FOLDER.tar.gz
tar -xzf $SQOOP_FOLDER.tar.gz

##copy data from S3 to hdfs
HADOOP_INPUT=hdfs:///user/hadoop/cascading
hadoop distcp s3://$BUCKET_NAME/output/cascading $HADOOP_INPUT

~/$SQOOP_FOLDER/bin/sqoop export --connect jdbc:mysql://servername.us-east-1.rds.amazonaws.com:3306/test --username myusername --password mypassword --table word_count --export-dir $HADOOP_INPUT --input-fields-terminated-by='\t' --update-mode insertonly
