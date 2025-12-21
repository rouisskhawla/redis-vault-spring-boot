FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
ARG VAULT_CERT_FILE
COPY ${JAR_FILE} app.jar
COPY ${VAULT_CERT_FILE} /tmp/vault-cert.pem
RUN keytool -import -trustcacerts -noprompt -alias vault \
    -file /tmp/vault-cert.pem \
    -keystore /etc/ssl/certs/java/cacerts \
    -storepass changeit
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app.jar"]
EXPOSE 8557
