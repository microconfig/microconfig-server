.graalvm/bin/java -agentlib:native-image-agent=config-merge-dir=server-microctl/src/main/resources/META-INF/native-image \
  -jar server-microctl/build/libs/microctl.jar $1
