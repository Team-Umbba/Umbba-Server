NOW_TIME="$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)"

BUILD_PATH=/home/ubuntu/notification-server/umbba-notification-0.0.1-SNAPSHOT.jar
TARGET_PORT=8083
TARGET_PID=$(lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')

if [ ! -z ${TARGET_PID} ]; then
  echo "[$NOW_TIME] Kill WAS running at ${TARGET_PORT}." >> /home/ubuntu/notification-server/deploy.log
  sudo kill -15 ${TARGET_PID}
fi

nohup java -jar -Duser.timezone=Asia/Seoul -Dspring.profiles.active=notification $BUILD_PATH >> /home/ubuntu/notification-server/deploy.log 2>/home/ubuntu/notification-server/deploy_err.log &
echo "[$NOW_TIME] Now new WAS runs at ${TARGET_PORT}." >> /home/ubuntu/notification-server/deploy.log
exit 0