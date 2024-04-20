#!/bin/bash
NOW_TIME="$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)"

BUILD_PATH=$(ls /home/ubuntu/api-server/umbba-api-0.0.1-SNAPSHOT.jar)
JAR_NAME=$(basename $BUILD_PATH)
echo "[$NOW_TIME] build 파일명: $JAR_NAME"

echo "[$NOW_TIME] build 파일 복사"
DEPLOY_PATH=/home/ubuntu/api-server/nonstop/jar/
cp $BUILD_PATH $DEPLOY_PATH

echo "[$NOW_TIME] 현재 구동중인 Prod 확인"
CURRENT_PROFILE=$(curl -s http://localhost/profile)
echo "[$NOW_TIME] $CURRENT_PROFILE"

# 쉬고 있는 prod 찾기: dev1이 사용중이면 dev2가 쉬고 있고, 반대면 dev1이 쉬고 있음
if [ $CURRENT_PROFILE == dev1 ]
then
  IDLE_PROFILE=dev2
  IDLE_PORT=8082
elif [ $CURRENT_PROFILE == dev2 ]
then
  IDLE_PROFILE=dev1
  IDLE_PORT=8081
else
  echo "[$NOW_TIME] 일치하는 Profile이 없습니다. Profile: $CURRENT_PROFILE"
  echo "[$NOW_TIME] dev1을 할당합니다. IDLE_PROFILE: dev1"
  IDLE_PROFILE=dev1
  IDLE_PORT=8081
fi

echo "[$NOW_TIME] application.jar 교체"
IDLE_APPLICATION=$IDLE_PROFILE-Umbba-API.jar
IDLE_APPLICATION_PATH=$DEPLOY_PATH$IDLE_APPLICATION

ln -Tfs $DEPLOY_PATH$JAR_NAME $IDLE_APPLICATION_PATH

echo "[$NOW_TIME] $IDLE_PROFILE 에서 구동중인 애플리케이션 pid 확인"
IDLE_PID=$(pgrep -f $IDLE_APPLICATION)

if [ -z $IDLE_PID ]
then
  echo "[$NOW_TIME] 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "[$NOW_TIME] kill -15 $IDLE_PID"
  kill -15 $IDLE_PID

  while ps -p $IDLE_PID > /dev/null; do
      sleep 1
    done
  echo "[$NOW_TIME] 애플리케이션이 정상 종료되었습니다."
fi

echo "[$NOW_TIME] $IDLE_PROFILE 배포"
nohup java -jar -Duser.timezone=Asia/Seoul -Dspring.profiles.active=$IDLE_PROFILE $IDLE_APPLICATION_PATH >> /home/ubuntu/api-server/deploy.log 2>/home/ubuntu/api-server/deploy_err.log &

##################################################################

echo "[$NOW_TIME] $IDLE_PROFILE 10초 후 Health check 시작"
echo "[$NOW_TIME] curl -s http://localhost:$IDLE_PORT/health "
sleep 10

for retry_count in {1..10}
do
  response=$(curl -s http://localhost:$IDLE_PORT/actuator/health)
  up_count=$(echo $response | grep 'UP' | wc -l)

  if [ $up_count -ge 1 ]
  then # $up_count >= 1 ("UP" 문자열이 있는지 검증)
      echo "[$NOW_TIME] Health check 성공"
      break
  else
      echo "[$NOW_TIME] Health check의 응답을 알 수 없거나 혹은 status가 UP이 아닙니다."
      echo "[$NOW_TIME] Health check: ${response}"
  fi

  if [ $retry_count -eq 10 ]
  then
    echo "[$NOW_TIME] Health check 실패. "
    echo "[$NOW_TIME] Nginx에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo "[$NOW_TIME] Health check 연결 실패. 재시도..."
  sleep 10
done