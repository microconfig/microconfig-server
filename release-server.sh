TAG="latest_alpha"

if [ -n "$1" ]
then
    TAG=$1
fi

docker build -t microconfig/server:"$TAG" -f server/Dockerfile server
docker push microconfig/server:"$TAG"