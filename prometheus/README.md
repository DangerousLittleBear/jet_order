# Prometheus & Grafana 모니터링 설정

이 폴더에는 Spring Boot 애플리케이션을 모니터링하기 위한 Prometheus와 Grafana 설정이 포함되어 있습니다.

## 필수 조건

- Docker와 Docker Compose가 설치되어 있어야 합니다.
- Spring Boot 애플리케이션에 Spring Actuator와 Micrometer Prometheus 의존성이 추가되어 있어야 합니다.

## Spring Boot 애플리케이션 설정

1. `build.gradle` 또는 `pom.xml`에 다음 의존성을 추가하세요:

```gradle
// Gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
```

```xml
<!-- Maven -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

2. `application.properties` 파일에 다음 설정을 추가하세요:

```properties
# Actuator 설정
management.endpoints.web.exposure.include=health,info,prometheus
management.endpoint.health.show-details=always
```

## 사용 방법

1. Spring Boot 애플리케이션을 실행합니다.
2. 다음 명령어로 Prometheus와 Grafana를 실행합니다:

```bash
./start.sh
```

3. 브라우저에서 다음 주소로 접속합니다:
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:3000 (기본 계정: admin/admin)

4. Grafana에 Prometheus 데이터 소스 추가하기:
   - Grafana에 로그인
   - 'Configuration' > 'Data Sources' > 'Add data source'
   - Prometheus 선택
   - URL에 `http://prometheus:9090` 입력
   - 'Save & Test' 클릭

## 주의사항

- `prometheus.yml` 파일의 `targets` 설정에서 Spring Boot 애플리케이션의 호스트와 포트가 올바르게 설정되어 있는지 확인하세요.
- 기본적으로 `host.docker.internal:8080`로 설정되어 있으며, 이는 Docker 컨테이너에서 호스트 머신의 8080 포트에 접근하기 위한 설정입니다.
- 애플리케이션 포트가 다르거나 별도의 호스트에서 실행 중이라면 해당 값을 적절히 수정하세요. 