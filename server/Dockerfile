FROM azul/zulu-openjdk-centos:15
EXPOSE 80

RUN yum -y install epel-release
RUN yum -y install jq

ADD docker-config.yaml /application.yaml
ADD build/libs/*.jar /server.jar

#-Xlog:gc*
CMD java -Dfile.encoding=UTF-8  \
    -XX:+UseShenandoahGC -XX:+UseCompressedOops -XX:ShenandoahGCHeuristics=compact \
    -Xms256m -Xmx256m \
    -jar /server.jar --spring.config.location=/application.yaml