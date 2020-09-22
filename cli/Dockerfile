FROM oracle/graalvm-ce:20.2.0-java11 AS builder
RUN gu install native-image

ADD build/libs/microctl.jar /microctl.jar
RUN native-image --static -jar /microctl.jar

FROM alpine:latest

COPY --from=builder /microctl /bin/microctl
RUN microctl version
CMD ["microctl"]