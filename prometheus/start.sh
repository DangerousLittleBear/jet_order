#!/bin/bash

# 스크립트가 위치한 디렉토리로 이동
cd "$(dirname "$0")"

# Docker Compose 실행
echo "Starting Prometheus and Grafana..."
docker-compose up -d

# 상태 확인
echo "Checking containers status..."
docker-compose ps

echo ""
echo "Prometheus is running at: http://localhost:9090"
echo "Grafana is running at: http://localhost:3000 (admin/admin)"
echo ""
