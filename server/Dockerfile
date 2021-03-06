FROM azul/zulu-openjdk-centos:15
EXPOSE 80

RUN yum -y install openssl; yum clean all

RUN mkdir /microconfig && mkdir /tls

ADD docker/docker-config.yaml /microconfig/application.yaml
ADD docker/docker-config-tls.yaml /microconfig/application-tls.yaml
ADD docker/starter.sh /microconfig/starter.sh

ADD build/libs/server.jar /microconfig/server.jar

CMD /microconfig/starter.sh