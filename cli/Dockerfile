FROM oracle/graalvm-ce:20.0.0-java11 AS builder
RUN gu install native-image

ADD build/libs/microconfig-cli.jar /microconfig-cli.jar
RUN native-image -jar /microconfig-cli.jar

FROM centos:latest

RUN mkdir /microconfig
ENV PATH="/microconfig:${PATH}"

COPY --from=builder /microconfig-cli /microconfig/microconfig-cli
CMD ["microconfig-cli"]