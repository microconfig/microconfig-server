./gradlew shadowJar -p cli
graalvm/native-image \
    --enable-all-security-services \
    -jar cli/build/libs/microconfig-cli.jar