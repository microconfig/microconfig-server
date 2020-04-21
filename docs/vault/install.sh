export VAULT_ADDR=http://localhost:8200
export VAULT_TOKEN=$1

vault secrets disable secret
vault secrets enable -path=secrets kv-v2

vault kv put secrets/dev/db user=dev-user pass=dev-password
vault kv put secrets/test/db user=test-user pass=test-password
vault kv put secrets/prod/db user=prod-user pass=prod-password

vault policy write policy-dev files/policy-dev.hcl
vault policy write policy-test files/policy-test.hcl
vault policy write policy-prod files/policy-prod.hcl

vault auth enable -path=approle approle
vault write auth/approle/role/dev-role token_policies=policy-dev
vault write auth/approle/role/test-role token_policies=policy-test
vault write auth/approle/role/prod-role token_policies=policy-prod

echo "dev-role role=$(vault read -field=role_id auth/approle/role/dev-role/role-id) secret=$(vault write -f -field=secret_id auth/approle/role/dev-role/secret-id)" > roles.txt
echo "test-role role=$(vault read -field=role_id auth/approle/role/test-role/role-id) secret=$(vault write -f -field=secret_id auth/approle/role/test-role/secret-id)" >> roles.txt
echo "prod-role role=$(vault read -field=role_id auth/approle/role/prod-role/role-id) secret=$(vault write -f -field=secret_id auth/approle/role/prod-role/secret-id)" >> roles.txt

vault auth enable -path=cluster kubernetes
vault write auth/cluster/config \
    kubernetes_host=https://kubernetes.default \
    kubernetes_ca_cert=@ca.crt

vault write auth/cluster/role/dev bound_service_account_names=vault-auth bound_service_account_namespaces=dev policies=policy-dev
vault write auth/cluster/role/test bound_service_account_names=vault-auth bound_service_account_namespaces=test policies=policy-test
vault write auth/cluster/role/prod bound_service_account_names=vault-auth bound_service_account_namespaces=prod policies=policy-prod
