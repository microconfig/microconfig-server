Launch kubernetes in docker desktop.
Master endpoint: `https://kubernetes.docker.internal:6443`
Dev Server from inside cluster `http://docker.for.mac.localhost:8080`
Inside Server `http://server.default.svc.cluster.local`

```shell script
kubectl create ns dev
kubectl create ns prod
kubectl apply -f yaml/sa.yaml
kubectl apply -f yaml/cli.yaml
```