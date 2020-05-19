kubectl apply -f files/vault.yaml
kubectl config view --raw -o json | jq -r '.clusters[] | select(.name == "docker-desktop")| .cluster."certificate-authority-data"' | base64 -D > files/ca.crt