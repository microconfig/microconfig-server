export VAULT_ADDR=http://localhost:8200
export VAULT_TOKEN=$1

vault secrets disable secret
vault secrets enable -path=team-secrets kv-v2

vault kv put team-secrets/dev/db user=dev-user pass=dev-password
vault kv put team-secrets/test/db user=test-user pass=test-password
vault kv put team-secrets/prod/db user=prod-user pass=prod-password

vault policy write team-policy-dev files/team-policy-dev.hcl
vault policy write team-policy-test files/team-policy-test.hcl
vault policy write team-policy-prod files/team-policy-prod.hcl

vault auth enable -path=team-approle approle
vault write auth/team-approle/role/team-dev-role token_policies=team-policy-dev
vault write auth/team-approle/role/team-test-role token_policies=team-policy-test
vault write auth/team-approle/role/team-prod-role token_policies=team-policy-prod

echo "team-dev-role role=$(vault read -field=role_id auth/team-approle/role/team-dev-role/role-id) secret=$(vault write -f -field=secret_id auth/team-approle/role/team-dev-role/secret-id)" > roles.txt
echo "team-test-role role=$(vault read -field=role_id auth/team-approle/role/team-test-role/role-id) secret=$(vault write -f -field=secret_id auth/team-approle/role/team-test-role/secret-id)" >> roles.txt
echo "team-prod-role role=$(vault read -field=role_id auth/team-approle/role/team-prod-role/role-id) secret=$(vault write -f -field=secret_id auth/team-approle/role/team-prod-role/secret-id)" >> roles.txt

vault auth enable -path=team-cluster kubernetes
vault write auth/team-cluster/config \
    token_reviewer_jwt=@account.jwt \
    kubernetes_host=https://kubernetes.docker.internal:6443 \
    kubernetes_ca_cert=@ca.crt

vault write auth/team-cluster/role/team-dev bound_service_account_names=team-dev bound_service_account_namespaces=default policies=team-policy-dev
vault write auth/team-cluster/role/team-test bound_service_account_names=team-test bound_service_account_namespaces=default policies=team-policy-test
vault write auth/team-cluster/role/team-prod bound_service_account_names=team-prod bound_service_account_namespaces=default policies=team-policy-prod
