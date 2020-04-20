FROM azul/zulu-openjdk-centos:14
EXPOSE 80

RUN yum -y install epel-release
RUN yum -y install jq

ADD docker-config.yaml /application.yaml
ADD build/libs/*.jar /server.jar

#-Xlog:gc*
CMD java -Dfile.encoding=UTF-8  \
    -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC -XX:+UseCompressedOops \
    -Xms256m -Xmx256m -XX:ShenandoahGCHeuristics=compact \
    -jar /server.jar --spring.config.location=/application.yaml