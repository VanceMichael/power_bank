# 第一阶段：使用 Maven 构建项目
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 只复制 pom.xml，利用 Docker 缓存
COPY pom.xml .

# 下载依赖（在 pom.xml 没有变化的情况下会使用缓存）
RUN mvn dependency:go-offline -DskipTests

# 复制项目源代码
COPY src ./src

# 构建项目，跳过测试
RUN mvn package -DskipTests -Dmaven.test.skip=true

# 第二阶段：使用 JRE 运行应用
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 配置时区
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# 配置环境变量
ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

# 从构建阶段复制 jar 文件
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 运行应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
