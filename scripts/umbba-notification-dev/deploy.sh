#!/bin/bash
NOW_TIME="$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)"

BUILD_PATH=/home/ubuntu/notification-server/umbba-notification-0.0.1-SNAPSHOT.jar
TARGET_PORT=8083
TARGET_PID=$(lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')

if [ ! -z ${TARGET_PID} ]; then
  echo "[$NOW_TIME] Kill WAS running at ${TARGET_PORT}." >> /home/ubuntu/notification-server/deploy.log
  sudo kill -15 ${TARGET_PID}
  while ps -p $TARGET_PID > /dev/null; do
      sleep 1
    done
  echo "[$NOW_TIME] 애플리케이션이 정상 종료되었습니다."
fi

nohup java -jar -Duser.timezone=Asia/Seoul -Dspring.profiles.active=dev $BUILD_PATH >> /home/ubuntu/notification-server/deploy.log 2>/home/ubuntu/notification-server/deploy_err.log &
echo "[$NOW_TIME] Now new WAS runs at ${TARGET_PORT}." >> /home/ubuntu/notification-server/deploy.log
exit 0