TAG="latest_beta"

if [ -n "$1" ]
then
    TAG=$1
fi

./gradlew shadowJar -p server-microctl

docker build -t microconfig/server-cli:"$TAG" -t microconfig/server-cli:latest server-microctl
docker push microconfig/server-cli:"$TAG"
docker push microconfig/server-cli:latest
