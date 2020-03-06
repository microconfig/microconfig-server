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
vault policy write team-policy-dev vault-policies/team-policy-dev.hcl
vault policy write team-policy-test vault-policies/team-policy-test.hcl
vault policy write team-policy-prod vault-policies/team-policy-prod.hcl
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
echo "team-dev-role role=$(vault read -field=role_id auth/team-approle/role/team-dev-role/role-id) secret=$(vault write -f -field=secret_id auth/team-approle/role/team-dev-role/secret-id)"
echo "team-test-role role=$(vault read -field=role_id auth/team-approle/role/team-test-role/role-id) secret=$(vault write -f -field=secret_id auth/team-approle/role/team-test-role/secret-id)"
echo "team-prod-role role=$(vault read -field=role_id auth/team-approle/role/team-prod-role/role-id) secret=$(vault write -f -field=secret_id auth/team-approle/role/team-prod-role/secret-id)"
```

#### Example

```shell script
team-dev-role role=08deab0a-e0b8-d408-f26e-418236a38a1d secret=bed04d48-af72-dd4d-8491-ebcef70b1395
team-test-role role=526cae12-6958-b99e-bfaa-3dde5d0ee5b1 secret=0a29f2b9-2b1a-de03-5073-f6bbed65f397
team-prod-role role=40f4d14c-87b9-6cc5-b9b0-173f6ebc967b secret=76e8f73e-6aa6-257f-fd07-46966c278e8a
```

#### Verify
```shell script
vault write auth/team-approle/login role_id="08deab0a-e0b8-d408-f26e-418236a38a1d" secret_id="bed04d48-af72-dd4d-8491-ebcef70b1395"
vault login token
vault kv get team-secrets/dev/db
vault kv get team-secrets/prod/db
```