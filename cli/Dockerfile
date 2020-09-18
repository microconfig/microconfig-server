FROM oracle/graalvm-ce:20.2.0-java11 AS builder
RUN gu install native-image

ADD build/libs/microctl.jar /microctl.jar
RUN native-image -jar /microctl.jar

FROM centos:latest

COPY --from=builder /microctl /usr/local/bin/microctl
CMD ["microctl"]