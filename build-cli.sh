./gradlew shadowJar -p cli
native-image \
    --enable-all-security-services \
    -jar cli/build/libs/microconfig-cli.jar