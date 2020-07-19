

## How to configure
Top3Finder 클래스 내 아래 코드를 환경에 맞게 수정합니다.
```
static final String DB_URL = "jdbc:mysql://{HOST}:{PORT}/kakaobank?serverTimezone=UTC";
static final String USERNAME = "{USERNAME}";
static final String PASSWORD = "{PASSWORD}";
```

## How to build
mvn install 하여 /target 디렉토리 내에 jar 파일이 성공적으로 생성되는지 확인합니다.

## How to run
/target 디렉토리 내 `menu_route_finder-1.0.0-jar-with-dependencies.jar` 파일로 프로그램을 구동합니다.
```
java -jar menu_route_finder-1.0.0-jar-with-dependencies.jar {결과파일 생성디렉토리}

e.g.
java -jar menu_route_finder-1.0.0-jar-with-dependencies.jar C:\
```