DEV_SECRET=$(kubectl get serviceaccount team-dev -n default -o jsonpath='{.secrets[0].name}')
kubectl get secret "$DEV_SECRET" -n default -o jsonpath='{.data.token}' | base64 -D > jwt_dev.jwt

TEST_SECRET=$(kubectl get serviceaccount team-test -n default -o jsonpath='{.secrets[0].name}')
kubectl get secret "$TEST_SECRET" -n default -o jsonpath='{.data.token}' | base64 -D > jwt_test.jwt

PROD_SECRET=$(kubectl get serviceaccount team-prod -n default -o jsonpath='{.secrets[0].name}')
kubectl get secret "$PROD_SECRET" -n default -o jsonpath='{.data.token}' | base64 -D > jwt_prod.jwt