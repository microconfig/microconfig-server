FROM azul/zulu-openjdk-centos:15
EXPOSE 80

ADD docker-config.yaml /application.yaml
ADD build/libs/server.jar /server.jar

#-Xlog:gc*
CMD java -Dfile.encoding=UTF-8  \
    -XX:+UseShenandoahGC -XX:+UseCompressedOops \
    -Xmx256m \
    -jar /server.jar --spring.config.location=/application.yaml