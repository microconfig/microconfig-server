TAG="latest_beta"

if [ -n "$1" ]
then
    TAG=$1
fi

./gradlew build -p server
docker build -t microconfig/server:"$TAG" -t microconfig/server:latest -f server/Dockerfile server
docker push microconfig/server:"$TAG"
docker push microconfig/server:latest