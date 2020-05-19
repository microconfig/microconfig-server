export GRAAL_HOME="$PWD/.graal"
./gradlew shadowJar -p cli
.graalvm/bin/native-image -jar cli/build/libs/microconfig-cli.jar