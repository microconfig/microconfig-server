Launch kubernetes in docker desktop.
Master endpoint: `https://kubernetes.docker.internal:6443`
Server from inside cluster `http://docker.for.mac.localhost:8080`

```shell script
kubectl apply -f yaml/cli.yaml
kubectl apply -f yaml/vault-sa.yaml
kubectl apply -f yaml/team-sa.yaml
```

```yaml
kubectl exec microconfig-cli-dev -- cat /var/run/secrets/kubernetes.io/serviceaccount/ca.crt
kubectl exec microconfig-cli-dev -- cat /var/run/secrets/kubernetes.io/serviceaccount/token
```