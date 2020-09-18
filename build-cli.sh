export GRAAL_HOME="$PWD/.graalvm"
./gradlew shadowJar -p cli
.graalvm/bin/native-image -jar cli/build/libs/microctl.jar