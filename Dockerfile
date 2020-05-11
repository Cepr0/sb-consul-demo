FROM consul:latest
VOLUME /tmp
RUN apk add openjdk11 --repository=http://dl-cdn.alpinelinux.org/alpine/edge/community

WORKDIR /service
COPY target/*.jar service.jar

#ARG CONSUL_SERVER_1=consul-server-1
#ARG CONSUL_SERVER_2=consul-server-2
#ARG CONSUL_SERVER_3=consul-server-3

ENTRYPOINT \
consul agent -client 0.0.0.0 -data-dir /var/consul -retry-join consul-server-1 -retry-join consul-server-2 -retry-join consul-server-3 & \
java -server -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar service.jar
