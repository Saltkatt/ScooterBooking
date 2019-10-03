# Booking api for scooters

A serverless rest api built with java and Amazon Web Services to book electric scooters in. 
The system is a backend solution where we use:

* AWS Lambdas
* DynamoDB
* Api Gateway
* Cognito


## Getting Started

These instructions will get you a copy of the project up and running for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Environment in where you can run bash scripts with the following programs installed (for windows, tested with ubuntu 18.04 lts from microsoft app store) 

```
jdk 8 or higher
maven 3.6.1
docker 19.03.1-ce
amazon/dynamodb-local running on port 8000 (docker run -d -p 8000:8000 amazon/dynamodb-local)
sam 0.19
aws-cli 1.16.215 (needs to be configured using aws configure)
jq 1.6

```


## Running the tests

In order to run the tests you need to have the dynamodb-local running in the background. 

```
docker run -d -p 8000:8000 amazon/dynamodb-local
mvn test
```
The tests consists of unit test and integration test with the local dynamodb.
A test bot is currently being worked on. This bot tests our api from the outside, first getting a valid jwt token
and then making crud test directly on our endpoints.

### Sam local tests

To run our integration test run:

```
docker run -d -p 8000:8000 amazon/dynamodb-local
mvn clean package
sam local start-api --env-vars testenvironment.json --docker-network=host
mvn surefire:test -Dtest=LocalSamTest
```


### Deploying

The stack is created by running the script cf-deploy script.

You need to set the parameters for STACK_NAME and BUCKET_NAME parameters in this script before you deploy:

```
STACK_NAME="mystackname"
TEMPLATE="packaged.yml"
PARAMETERS_FILE="parameters.json"
PARAMS=($(jq -r '.Parameters[] | [.ParameterKey, .ParameterValue] | "\(.[0])=\(.[1])"' ${PARAMETERS_FILE}))
BUCKET_NAME="mybucketname"

mvn clean package && \
aws cloudformation package     --template-file template.yml     --output-template-file packaged.yml     --s3-bucket $BUCKET_NAME && \


aws cloudformation deploy \
	    --template-file "${TEMPLATE}" \
	        --stack-name "${STACK_NAME}" \
		    --parameter-overrides ${PARAMS[@]} \
		    --capabilities CAPABILITY_IAM

```

The bucket needs to be created before deploying. mybucketname is the name of your bucket.

```
aws s3 mb s3://mybucketname

```

The script reads from parameters.json where we set parameters the sam template.yml. 
This file needs to be put in the root folder of the project. 
The following is an example of the configuration of this file:

```
{
  "Parameters": [
    {
      "ParameterKey": "UserPoolName",
      "ParameterValue": "theUserPoolNamethatYouWantToset"
    },
    {
      "ParameterKey": "LogoutUrl",
      "ParameterValue": "http://localhost:8080/logout"
    },
    {
      "ParameterKey": "CallbackUrl",
      "ParameterValue": "http://localhost:8080/login/oauth2/code/cognito"
    },
    {
      "ParameterKey": "CallbackHostedUrl",
      "ParameterValue": "https://yourHostedDomain.com/login/oauth2/code/cognito"
    },
    {
      "ParameterKey": "CallbackHostedLogoutUrl",
      "ParameterValue": "https://yourHostedDomain.com/logout"
    },
    {
      "ParameterKey": "SourceArnEmail",
      "ParameterValue": "arn:aws:ses:us-east-1:**********:identity/youremail@email.com"
    },
    {
      "ParameterKey": "SmsExternalId",
      "ParameterValue": "111eee65-153a-1235-9856-1ea3c3dd11e1"
    },
    {
      "ParameterKey": "SmsArn",
      "ParameterValue": "arn:aws:iam::******:role/service-role/nameofyoursmsrole"
    },
    {
      "ParameterKey": "TableName",
      "ParameterValue": "yourdynamodbtablename"
    },
    {
      "ParameterKey": "BucketName",
      "ParameterValue": "yourbucketnamefors3configfile"
    }
  ]
}

```

Run the script:

```
./cf-deploy
```

After the stack creation is complete you can update over it using cf-deploy again. If the name of the 
resources of the DynamoDB table and Cognito userpool already exists it will not create new resources.

In order to set the correct scopes on the endpoint for amazon cognito you may also run the
updates-scopes script:

```
./update-scopes yourstackname
```
This script sets email, openid and aws.cognito.signin.user.admin as scopes on all endpoints.
This can also be done manually in the api gateway. There is no way to do this in the sam template at the moment 
but a fix is being worked on. https://github.com/awslabs/serverless-application-model/issues/652

## Frontend for amazon cognito login

We also included simple frontend that we use to connect to cognito to get the jwt token to call the api.
In order to run this locally you need to set this information in a .env file in the frontend directory:

```
VUE_APP_COGNITO_REDIRECT_URI=http://localhost:8080/login/oauth2/code/cognito
VUE_APP_COGNITO_REDIRECT_URI_SIGNOUT=http://localhost:8080/logout
VUE_APP_APP_URL=http://localhost:8080
```
And you need to set this in a .env.local file in the frontend directory
```
VUE_APP_COGNITO_USERPOOL_ID=youruserpoolid
VUE_APP_COGNITO_APP_DOMAIN=yourhostedappdomainincognito
VUE_APP_COGNITO_CLIENT_ID=yourcognitoclientid
```
and the run:
```
npm install
npm run serve
```
To start up the local vue server.
If you have problems using it double check that local server is running on port 8080. 



## Built With

* [docker](https://www.docker.com/) - For running local dynamodb and sam
* [maven](https://maven.apache.org/) - Dependency Management
* [openJDK 8](https://openjdk.java.net/install/) - Java
* [aws-cli](https://aws.amazon.com/cli/) - Used in deploy scripts
* [sam](https://aws.amazon.com/serverless/sam/) - Serverless Application Model - Used for integration tests.
