
Deploy cert-manager on Azure Kubernetes Service (AKS) 
and use Let's Encrypt to sign a certificate for an HTTPS website


https://cert-manager.io/docs/tutorials/getting-started-aks-letsencrypt/


export AZURE_DEFAULTS_GROUP=nag-rg
export AZURE_DEFAULTS_LOCATION=centralindia
export DOMAIN_NAME=nagcloudlab.com

az network dns zone create --name nagcloudlab.com
az network dns zone show --name $DOMAIN_NAME --output yaml
dig $DOMAIN_NAME ns +trace +nodnssec


Install AKS

kubectl get nodes -o wide


Install cert-manager


helm repo add jetstack https://charts.jetstack.io
helm repo update
helm upgrade cert-manager jetstack/cert-manager \
    --install \
    --create-namespace \
    --wait \
    --namespace cert-manager \
    --set installCRDs=true

kubectl -n cert-manager get all


kubectl explain Certificate
kubectl explain CertificateRequest
kubectl explain Issuer


Create an Issuer

kubectl apply -f clusterissuer-selfsigned.yaml
envsubst < certificate.yaml | kubectl apply -f -

cmctl status certificate www
cmctl inspect secret www-tls


Deploy a sample web server


kubectl apply -f deployment.yaml

export AZURE_LOADBALANCER_DNS_LABEL_NAME=lb-$(uuidgen)
envsubst < service.yaml | kubectl apply -f -

kubectl get service helloweb

az network dns record-set cname set-record \
    --zone-name $DOMAIN_NAME \
    --cname $AZURE_LOADBALANCER_DNS_LABEL_NAME.$AZURE_DEFAULTS_LOCATION.cloudapp.azure.com \
    --record-set-name www

dig www.$DOMAIN_NAME A

curl --insecure -v https://www.$DOMAIN_NAME



-----------------------------------------------------------------------------


az extension add --name aks-preview


az feature register --namespace "Microsoft.ContainerService" --name "EnableWorkloadIdentityPreview"

# It takes a few minutes for the status to show Registered. Verify the registration status by using the az feature list command:

az feature list -o table --query "[?contains(name, 'Microsoft.ContainerService/EnableWorkloadIdentityPreview')].{Name:name,State:properties.state}"

# When ready, refresh the registration of the Microsoft.ContainerService resource provider by using the az provider register command:
az provider register --namespace Microsoft.ContainerService


Reconfigure the cluster

az aks update \
    --name nag-aks \
    --enable-oidc-issuer \
    --enable-workload-identity 

Reconfigure cert-manager

helm upgrade cert-manager jetstack/cert-manager \
    --namespace cert-manager \
    --reuse-values \
    --values values.yaml

kubectl describe pod -n cert-manager -l app.kubernetes.io/component=controller



Create an Azure Managed Identity

export USER_ASSIGNED_IDENTITY_NAME=cert-manager-tutorials-1 
az identity create --name "${USER_ASSIGNED_IDENTITY_NAME}"
export USER_ASSIGNED_IDENTITY_CLIENT_ID=$(az identity show --name "${USER_ASSIGNED_IDENTITY_NAME}" --query 'clientId' -o tsv)
az role assignment create \
    --role "DNS Zone Contributor" \
    --assignee $USER_ASSIGNED_IDENTITY_CLIENT_ID \
    --scope $(az network dns zone show --name $DOMAIN_NAME -o tsv --query id)

Add a federated identity

export SERVICE_ACCOUNT_NAME=cert-manager 
export SERVICE_ACCOUNT_NAMESPACE=cert-manager 

export SERVICE_ACCOUNT_ISSUER=$(az aks show --resource-group $AZURE_DEFAULTS_GROUP --name nag-aks --query "oidcIssuerProfile.issuerUrl" -o tsv)

az identity federated-credential create \
  --name "cert-manager" \
  --identity-name "${USER_ASSIGNED_IDENTITY_NAME}" \
  --issuer "${SERVICE_ACCOUNT_ISSUER}" \
  --subject "system:serviceaccount:${SERVICE_ACCOUNT_NAMESPACE}:${SERVICE_ACCOUNT_NAME}"


Create a ClusterIssuer for Let's Encrypt Staging


export EMAIL_ADDRESS=nagtraininglab@gmail.com
export AZURE_SUBSCRIPTION=Pay-As-You-Go
export AZURE_SUBSCRIPTION_ID=$(az account show --name $AZURE_SUBSCRIPTION --query 'id' -o tsv)
envsubst < clusterissuer-lets-encrypt-staging.yaml | kubectl apply -f  -
kubectl describe clusterissuer letsencrypt-staging


Re-issue the Certificate using Let's Encrypt

kubectl patch certificate www --type merge  -p '{"spec":{"issuerRef":{"name":"letsencrypt-staging"}}}'


cmctl status certificate www
cmctl inspect secret www-tls

kubectl rollout restart deployment helloweb
curl -v --insecure https://www.$DOMAIN_NAME

envsubst < clusterissuer-lets-encrypt-production.yaml | kubectl apply -f  -
kubectl describe clusterissuer letsencrypt-production

kubectl patch certificate www --type merge  -p '{"spec":{"issuerRef":{"name":"letsencrypt-production"}}}'
cmctl status certificate www
cmctl inspect secret www-tls

kubectl rollout restart deployment helloweb
curl -v https://www.$DOMAIN_NAME



clusterissuer-lets-encrypt-production.yaml

```yaml
# clusterissuer-lets-encrypt-production.yaml
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-production
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: nagtraininglab@gmail.com
    privateKeySecretRef:
      name: letsencrypt-production
    solvers:
      - dns01:
          azureDNS:
            resourceGroupName: nag-rg
            subscriptionID: 9ecbf5b8-8ab3-4e90-b81d-8bd777197b07
            hostedZoneName: nagcloudlab.com
            environment: AzurePublicCloud
            managedIdentity:
              clientID: aecc60a3-2cb6-482e-987e-0d1e276f4eff
```
