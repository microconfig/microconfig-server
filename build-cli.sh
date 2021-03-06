export GRAAL_HOME="$PWD/.graalvm"
./gradlew shadowJar -p server-microctl
.graalvm/bin/native-image -jar server-microctl/build/libs/microctl.jar