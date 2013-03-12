BUCKET_NAME=zli-emr-test

cd ~

##Install file to data node on emr
FILE=mysql-connector-java-5.1.19.jar
ips=`hadoop dfsadmin -report | grep ^Name | cut -f2 -d: | cut -f2 -d' '`
for ip in $ips
do
    ssh -i id_zli-emr.pem $ip "hadoop fs -copyToLocal s3://$BUCKET_NAME/$FILE $FILE"
done