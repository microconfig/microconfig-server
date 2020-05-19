## kubernetes auth

### full kubernetes auth
```shell script
microconfig-cli config payment-backend --branch vault -e dev \
 --set microconfig.vault.address=http://vault.default.svc.cluster.local \
 --set microconfig.vault.auth=kubernetes \
 --set microconfig.vault.kubernetes.path=cluster \
 --set microconfig.vault.kubernetes.role=dev \
 --set microconfig.vault.kubernetes.jwt=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
```

### short kubernetes auth
```shell script
microconfig-cli config payment-backend --branch vault -e dev \
 --set microconfig.vault.kubernetes.jwt=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)

microconfig-cli config payment-backend --branch vault -e prod \
 --set microconfig.vault.kubernetes.jwt=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)
```

### token auth
```shell script
./microconfig-cli config payment-backend --branch vault -e dev \
 --set microconfig.vault.address=http://localhost:8200 \
 --set microconfig.vault.auth=token \
 --set microconfig.vault.token=$(cat ~/.vault-token)
```

### approle 
```shell script
./microconfig-cli config payment-backend --branch vault -e dev \
 --set microconfig.vault.address=http://localhost:8200 \
 --set microconfig.vault.auth=approle \
 --set microconfig.vault.approle.path=team-approle \
 --set microconfig.vault.approle.role=7b12757b-0dbf-f5e6-b036-045a6b402e75 \
 --set microconfig.vault.approle.secret=4125dc06-bbd9-45c6-9ae4-ac1fc9c8933a
```