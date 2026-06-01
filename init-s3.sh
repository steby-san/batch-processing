#!/bin/bash

BUCKET_NAME=my-batch-bucket
CONTAINER_NAME=batch-processing-localstack-1

CONTAINER_ID=$(docker ps -qf "name=localstack")

if [ -z "$CONTAINER_ID" ]; then
    echo "LocalStack container is not running. Please start it with 'docker-compose up -d'"
    exit 1
fi

echo "Waiting for LocalStack to be ready..."
sleep 5

echo "Creating S3 bucket: $BUCKET_NAME"
docker exec $CONTAINER_ID awslocal s3 mb s3://$BUCKET_NAME

echo "Copying files to LocalStack container..."
docker cp test-data/transaction.csv $CONTAINER_ID:/tmp/transaction.csv
docker cp test-data/employee.csv $CONTAINER_ID:/tmp/employee.csv

echo "Uploading test files to S3..."
docker exec $CONTAINER_ID awslocal s3 cp /tmp/transaction.csv s3://$BUCKET_NAME/transaction.csv
docker exec $CONTAINER_ID awslocal s3 cp /tmp/employee.csv s3://$BUCKET_NAME/employee.csv

echo "Listing files in S3 bucket:"
docker exec $CONTAINER_ID awslocal s3 ls s3://$BUCKET_NAME/

echo "Done!"
