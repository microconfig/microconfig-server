`docker-compose up`

Note Vault root token in logs.

```$xslt
vault_1  | Unseal Key: ...
vault_1  | Root Token: ...
```

`export VAULT_ADDR=http://localhost:8200`

#### Initial setup
```shell script
vault login ROOT_TOKEN
vault secrets disable secret
vault secrets enable -path=team-secrets kv-v2
```

#### Adding secrets
```shell script
vault kv put team-secrets/dev/db user=dev-user pass=dev-password
vault kv put team-secrets/test/db user=test-user pass=test-password
vault kv put team-secrets/prod/db user=prod-user pass=prod-password
```

#### Policies for secrets access
```shell script
vault policy write team-policy-dev files/team-policy-dev.hcl
vault policy write team-policy-test files/team-policy-test.hcl
vault policy write team-policy-prod files/team-policy-prod.hcl
```

#### App roles
```shell script
vault auth enable -path=team-approle approle
vault write auth/team-approle/role/team-dev-role token_policies=team-policy-dev
vault write auth/team-approle/role/team-test-role token_policies=team-policy-test
vault write auth/team-approle/role/team-prod-role token_policies=team-policy-prod
```

#### RoleId / SecretId
```shell script
echo "team-dev-role role=$(vault read -field=role_id auth/team-approle/role/team-dev-role/role-id) secret=$(vault write -f -field=secret_id auth/team-approle/role/team-dev-role/secret-id)" > roles.txt
echo "team-test-role role=$(vault read -field=role_id auth/team-approle/role/team-test-role/role-id) secret=$(vault write -f -field=secret_id auth/team-approle/role/team-test-role/secret-id)" >> roles.txt
echo "team-prod-role role=$(vault read -field=role_id auth/team-approle/role/team-prod-role/role-id) secret=$(vault write -f -field=secret_id auth/team-approle/role/team-prod-role/secret-id)" >> roles.txt
```

#### Verify AppRole
```shell script
vault write auth/team-approle/login role_id="{ID}" secret_id="{SECRET}"
vault login token
vault kv get team-secrets/dev/db
vault kv get team-secrets/prod/db
```

#### Kube Engine
```shell script
vault auth enable -path=team-cluster kubernetes
vault write auth/team-cluster/config \
    token_reviewer_jwt=@account.jwt \
    kubernetes_host=https://kubernetes.docker.internal:6443 \
    kubernetes_ca_cert=@ca.crt
```

#### Kube Roles
```shell script
vault write auth/team-cluster/role/team-dev bound_service_account_names=team-dev bound_service_account_namespaces=default policies=team-policy-dev
vault write auth/team-cluster/role/team-test bound_service_account_names=team-test bound_service_account_namespaces=default policies=team-policy-test
vault write auth/team-cluster/role/team-prod bound_service_account_names=team-prod bound_service_account_namespaces=default policies=team-policy-prod
```

