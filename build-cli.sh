./gradlew shadowJar -p cli
native-image -jar cli/build/libs/microconfig-cli.jar