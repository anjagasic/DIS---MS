#!/usr/bin/env bash
#
# ./grdelw clean build
# docker-compose build
# docker-compose up -d
#
# Sample usage:
#
#   HOST=localhost PORT=7000 ./test-em-all.bash
#
: ${HOST=localhost}
: ${PORT=8081}
: ${GYM_ID_CLIS_EMPS_PROGS=2}
: ${GYML_ID_NOT_FOUND=14}
: ${GYM_ID_NO_CLIS=114}
: ${GYM_ID_NO_EPMS=214}
: ${GYM_ID_NO_PROGS=314}

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
      echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
      echo  "- Failing command: $curlCmd"
      echo  "- Response Body: $RESPONSE"
      exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

function testUrl() {
    url=$@
    if curl $url -ks -f -o /dev/null
    then
          echo "Ok"
          return 0
    else
          echo -n "not yet"
          return 1
    fi;
}

function testCompositeCreated() {

    # Expect that the Gym Composite for gymId $GYM_ID_CLIS_EMPS_PROGS has been created with three clients, three employees and three programs
    if ! assertCurl 200 "curl http://$HOST:$PORT/gym-composite/$GYM_ID_CLIS_EMPS_PROGS -s"
    then
        echo -n "FAIL"
        return 1
    fi

    set +e
    assertEqual "$GYM_ID_CLIS_EMPS_PROGS" $(echo $RESPONSE | jq .gymId)
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".clients | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".employees | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    assertEqual 3 $(echo $RESPONSE | jq ".programs | length")
    if [ "$?" -eq "1" ] ; then return 1; fi

    set -e
}

function waitForService() {
    url=$@
    echo -n "Wait for: $url... "
    n=0
    until testUrl $url
    do
        n=$((n + 1))
        if [[ $n == 100 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
}

function waitForMessageProcessing() {
    echo "Wait for messages to be processed... "

    # Give background processing some time to complete...
    sleep 1

    n=0
    until testCompositeCreated
    do
        n=$((n + 1))
        if [[ $n == 40 ]]
        then
            echo " Give up"
            exit 1
        else
            sleep 6
            echo -n ", retry #$n "
        fi
    done
    echo "All messages are now processed!"
}

function recreateComposite() {
    local gymId=$1
    local composite=$2

    assertCurl 200 "curl -X DELETE http://$HOST:$PORT/gym-composite/${gymId} -s"
    curl -X POST http://$HOST:$PORT/gym-composite -H "Content-Type: application/json" --data "$composite"
}

function setupTestdata() {
       body="{\"gymId\":$GYM_ID_NO_CLIS"
        body+=\
  '{"gymId":1,"name":"name 1","address":"address 1",
     "employees":[
          {"employeeId":1,"fullName":"name 1"},
          {"employeeId":2,"fullName":"name 2"},
          {"employeeId":3,"fullName":"name 3"}
      ], "programs":[
          {"programId":1,"name":"name 1"},
          {"programId":2,"name":"name 2"},
          {"programId":3,"name":"name 3"}
      ]}'
       recreateComposite "$GYM_ID_NO_CLIS" "$body"


           body="{\"gymId\":$GYM_ID_NO_PROGS"
           body+=\
    '{"gymId":1,"name":"name 1","address":"address 1",
        "clients":[
            {"clientId":1,"fullName":"name 1","gender":"Female","age":"25"},
            {"clientId":2,"fullName":"name 2","gender":"Male","age":"25"},
            {"clientId":3,"fullName":"name 3","gender":"Female","age":"15"}
        ], "employees":[
            {"employeeId":1,"fullName":"name 1"},
            {"employeeId":2,"fullName":"name 2"},
            {"employeeId":3,"fullName":"name 3"}
        ]'
          recreateComposite "$GYM_ID_NO_PROGS" "$body"

        body="{\"gymId\":$GYM_ID_NO_EPMS"
        body+=\
    '{"gymId":1,"name":"name 1","address":"address 1",
        "clients":[
            {"clientId":1,"fullName":"name 1","gender":"Female","age":"25"},
            {"clientId":2,"fullName":"name 2","gender":"Male","age":"25"},
            {"clientId":3,"fullName":"name 3","gender":"Female","age":"15"}
        ], "programs":[
            {"programId":1,"name":"name 1"},
            {"programId":2,"name":"name 2"},
            {"programId":3,"name":"name 3"}
        ]}'
       recreateComposite "$GYM_ID_NO_EPMS" "$body"

        body="{\"gymId\":$GYM_ID_CLIS_EMPS_PROGS"
        body+=\
   '{"gymId":1,"name":"name 1","address":"address 1",
       "clients":[
           {"clientId":1,"fullName":"name 1","gender":"Female","age":"25"},
           {"clientId":2,"fullName":"name 2","gender":"Male","age":"25"},
           {"clientId":3,"fullName":"name 3","gender":"Female","age":"15"}
       ], "employees":[
           {"employeeId":1,"fullName":"name 1"},
           {"employeeId":2,"fullName":"name 2"},
           {"employeeId":3,"fullName":"name 3"}
       ], "programs":[
           {"programId":1,"name":"name 1"},
           {"programId":2,"name":"name 2"},
           {"programId":3,"name":"name 3"}
       ]}'

       recreateComposite "$GYM_ID_CLIS_EMPS_PROGS" "$body"
}

set -e

echo "Start:" `date`

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]
then
    echo "Restarting the test environment..."
    echo "$ docker-compose down"
    docker-compose down
    echo "$ docker-compose up -d"
    docker-compose up -d
fi

waitForService curl -X DELETE http://$HOST:$PORT/actuator/health
setupTestdata
waitForMessageProcessing

# Verify that a normal request works, expect three programs, three clients and three employees
assertCurl 200 "curl http://$HOST:$PORT/gym-composite/$GYM_ID_CLIS_EMPS_PROGS -s"
assertEqual "$GYM_ID_CLIS_EMPS_PROGS" $(echo $RESPONSE | jq .gymId)
assertEqual 3 $(echo $RESPONSE | jq ".programs | length")
assertEqual 3 $(echo $RESPONSE | jq ".clients | length")
assertEqual 3 $(echo $RESPONSE | jq ".employees | length")

# Verify that a 404 (Not Found) error is returned for a non existing gymId (13)
assertCurl 404 "curl http://$HOST:$PORT/gym-composite/$GYML_ID_NOT_FOUND -s"

# Verify that no clients are returned for gymId $GYM_ID_NO_CLIS
assertCurl 200 "curl http://$HOST:$PORT/gym-composite/$GYM_ID_NO_CLIS -s"
assertEqual "$GYM_ID_NO_CLIS" $(echo $RESPONSE | jq .gymId)
assertEqual 0 $(echo $RESPONSE | jq ".clients | length")
assertEqual 3 $(echo $RESPONSE | jq ".programs | length")
assertEqual 3 $(echo $RESPONSE | jq ".employees | length")

# Verify that no programs are returned for mealId $GYM_ID_NO_PROGS
assertCurl 200 "curl http://$HOST:$PORT/gym-composite/$GYM_ID_NO_PROGS -s"
assertEqual "$GYM_ID_NO_PROGS" $(echo $RESPONSE | jq .mealId)
assertEqual 3 $(echo $RESPONSE | jq ".clients | length")
assertEqual 0 $(echo $RESPONSE | jq ".programs | length")
assertEqual 3 $(echo $RESPONSE | jq ".employees | length")

# Verify that no ingredients are returned for mealId $GYM_ID_NO_EPMS
assertCurl 200 "curl http://$HOST:$PORT/gym-composite/$GYM_ID_NO_EPMS -s"
assertEqual "$GYM_ID_NO_EPMS" $(echo $RESPONSE | jq .mealId)
assertEqual 3 $(echo $RESPONSE | jq ".clients | length")
assertEqual 3 $(echo $RESPONSE | jq ".programs | length")
assertEqual 0 $(echo $RESPONSE | jq ".employees | length")

# Verify that a 422 (Unprocessable Entity) error is returned for a gymId that is out of range (-1)
assertCurl 422 "curl http://$HOST:$PORT/gym-composite/-1 -s"
assertEqual "\"Invalid gymId: -1\"" "$(echo $RESPONSE | jq .message)"

# Verify that a 400 (Bad Request) error error is returned for a gymId that is not a number, i.e. invalid format
assertCurl 400 "curl http://$HOST:$PORT/gym-composite/invalidGymId -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"

echo "End, all tests OK:" `date`

if [[ $@ == *"stop"* ]]
then
    echo "Stopping the test environment..."
    echo "$ docker-compose down --remove-orphans"
    docker-compose down --remove-orphans
fi