#!/bin/bash
BUCKET_NAME=zli-emr-test
SQOOP_FOLDER=sqoop-1.4.1-incubating__hadoop-0.20
SQOOP_TAR=$SQOOP_FOLDER.tar.gz

##change to home directory
cd ~

##Install sqoop on emr
hadoop fs -copyToLocal s3n://$BUCKET_NAME/$SQOOP_TAR $SQOOP_TAR
tar -xzf $SQOOP_TAR

##Install jdbc driver (ex mysql-connection-java.jar) to sqoop lib folder
hadoop fs -copyToLocal s3n://$BUCKET_NAME/mysql-connector-java-5.1.19.jar ~/$SQOOP_FOLDER/lib/

##Copy file from S3 to hdfs
HADOOP_INPUT=hdfs:///user/hadoop/cascading
hadoop distcp s3://$BUCKET_NAME/output/cascading $HADOOP_INPUT

~/$SQOOP_FOLDER/bin/sqoop export --connect jdbc:mysql://yourserver.us-east-1.rds.amazonaws.com:3306/test --username myusername --password mypassword --table word_count --export-dir $HADOOP_INPUT --input-fields-terminated-by='\t'
