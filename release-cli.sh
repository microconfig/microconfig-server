TAG="latest_beta"

if [ -n "$1" ]
then
    TAG=$1
fi

./gradlew shadowJar -p cli

docker build -t microconfig/server-cli:"$TAG" -t microconfig/server-cli:latest -f cli/Dockerfile cli
docker push microconfig/server-cli:"$TAG"
docker push microconfig/server-cli:latest
