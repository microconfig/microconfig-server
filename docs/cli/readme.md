### kubernetes auth

```shell script
./microconfig-cli config payment-db-patcher --branch vault -e test \
 --set microconfig.vault.address=http://localhost:8200 \
 --set microconfig.vault.auth=kubernetes \
 --set microconfig.vault.kubernetes.path=team-cluster \
 --set microconfig.vault.kubernetes.role=team-test \
 --set microconfig.vault.kubernetes.jwt=$(cat docs/kubernetes/jwt_test.jwt)
```

```shell script
./microconfig-cli config payment-db-patcher --branch vault -e test \
 --set microconfig.vault.kubernetes.jwt=$(cat docs/kubernetes/jwt_test.jwt)
```


### token auth
```shell script
./microconfig-cli config payment-db-patcher --branch vault -e test \
 --set microconfig.vault.address=http://localhost:8200 \
 --set microconfig.vault.auth=token \
 --set microconfig.vault.token=$(cat ~/.vault-token)
```

### approle 
```shell script
./microconfig-cli config payment-db-patcher --branch vault -e test \
 --set microconfig.vault.address=http://localhost:8200 \
 --set microconfig.vault.auth=approle \
 --set microconfig.vault.approle.path=team-approle \
 --set microconfig.vault.approle.role=1f4a82a4-ad6a-6262-01e5-d335bfba25b7 \
 --set microconfig.vault.approle.secret=429f2689-59bc-9b52-4936-e36f724c83e7
```