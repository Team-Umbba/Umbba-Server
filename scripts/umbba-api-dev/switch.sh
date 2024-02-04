#!/bin/bash
NOW_TIME="$(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)"

echo "[$NOW_TIME] 스위칭"
sleep 10
echo "[$NOW_TIME] 현재 구동중인 Port 확인"
CURRENT_PROFILE=$(curl -s http://localhost/profile)

# 쉬고 있는 prod 찾기: dev1이 사용중이면 dev2가 쉬고 있고, 반대면 dev1이 쉬고 있음
if [ $CURRENT_PROFILE == dev1 ]
then
  IDLE_PORT=8082
elif [ $CURRENT_PROFILE == dev2 ]
then
  IDLE_PORT=8081
else
  echo "[$NOW_TIME] 일치하는 Profile이 없습니다. Profile: $CURRENT_PROFILE"
  echo "[$NOW_TIME] 8081을 할당합니다."
  IDLE_PORT=8081
fi

echo "[$NOW_TIME] 전환할 Port: $IDLE_PORT"
echo "[$NOW_TIME] Port 전환"
echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc

PROXY_PORT=$(curl -s http://localhost/profile)
echo "[$NOW_TIME] Nginx Current Proxy Port: $PROXY_PORT"

echo "[$NOW_TIME] Nginx Reload"
sudo service nginx reload