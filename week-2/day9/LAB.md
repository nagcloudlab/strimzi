
-----------------------------------------------------
AKS setup
-----------------------------------------------------

az group create --name nag-rg --location centralindia

az aks create \
    --resource-group nag-rg \
    --name nag-aks \
    --generate-ssh-keys \
    --node-count 3 \
    --zones 1 2 3

kubectl get nodes -o wide
kubectl get nodes --show-labels

-----------------------------------------------------
ingress-nginx setup on AKS
-----------------------------------------------------

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm install ingress-nginx ingress-nginx/ingress-nginx \
  --create-namespace \
  --namespace kafka \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-health-probe-request-path"=/healthz
kubectl get services --namespace kafka -o wide ingress-nginx-controller

-----------------------------------------------------
🛑 update ingress-ngix's external-ip DNS records
-----------------------------------------------------

-----------------------------------------------------
SSL Passthrough
-----------------------------------------------------

kubectl get deployments --all-namespaces | grep ingress-nginx
kubectl edit deployment ingress-nginx-controller -n kafka

Add the SSL Passthrough Flag
```yaml
spec:
  template:
    spec:
      containers:
      - args:
        - /nginx-ingress-controller
        - --publish-service=$(POD_NAMESPACE)/ingress-nginx-controller
        - --election-id=ingress-controller-leader
        - --ingress-class=nginx
        - --configmap=$(POD_NAMESPACE)/ingress-nginx-controller
        - --validating-webhook=:8443
        - --validating-webhook-certificate=/usr/local/certificates/cert
        - --validating-webhook-key=/usr/local/certificates/key
        - --default-ssl-certificate=$(POD_NAMESPACE)/tls-secret
        - --enable-ssl-passthrough
        - --v=2
        image: k8s.gcr.io/ingress-nginx/controller:v1.0.0
```

kubectl rollout status deployment ingress-nginx-controller -n kafka
kubectl describe deployment ingress-nginx-controller -n kafka

-----------------------------------------------------
rack-awareness
-----------------------------------------------------

./docs/rack-awareness.md

https://docs.google.com/presentation/d/1pMTUxbKQqLo9Dq_oqL-ZSaXo8PU1cQPndUyQwGgox3w/edit#slide=id.g266c580a345_1_55

-----------------------------------------------------
Node Affinity
-----------------------------------------------------

./docs/node-affinity.md
https://docs.google.com/presentation/d/1pMTUxbKQqLo9Dq_oqL-ZSaXo8PU1cQPndUyQwGgox3w/edit#slide=id.g268704068db_0_4



k get nodes -o wide
kubectl taint nodes aks-nodepool1-38385085-vmss000003 dedicated=Kafka:NoSchedule
kubectl taint nodes aks-nodepool1-38385085-vmss000004 dedicated=Kafka:NoSchedule

kubectl taint nodes aks-nodepool1-38385085-vmss000003 dedicated-
kubectl taint nodes aks-nodepool1-38385085-vmss000004 dedicated-

kubectl label nodes aks-nodepool1-38385085-vmss000003 dedicated=Kafka
kubectl label nodes aks-nodepool1-38385085-vmss000004 dedicated=Kafka

kubectl label nodes aks-nodepool1-38385085-vmss000003 dedicated-
kubectl label nodes aks-nodepool1-38385085-vmss000004 dedicated-


-----------------------------------------------------
Deploy Strimzi Operator(s)
-----------------------------------------------------

Deploying the Cluster Operator

kubectl create namespace kafka
sed -i 's/namespace: .*/namespace: kafka/' ./strimzi-0.38.0/install/cluster-operator/*RoleBinding*.yaml

kubectl create -f ./strimzi-0.38.0/install/cluster-operator -n kafka
kubectl get deployment -n kafka
kubectl get pods -n kafka -o wide
kubectl logs deployment/strimzi-cluster-operator -n kafka 
kubectl delete -f ./strimzi-0.38.0/install/cluster-operator -n kafka

🛑 single replica 'cluster-operator' not safe for production

https://docs.google.com/presentation/d/1pMTUxbKQqLo9Dq_oqL-ZSaXo8PU1cQPndUyQwGgox3w/edit#slide=id.g268704068db_0_101


-----------------------------------------------------
Resources
-----------------------------------------------------

./doc/resources.md
https://docs.google.com/presentation/d/1pMTUxbKQqLo9Dq_oqL-ZSaXo8PU1cQPndUyQwGgox3w/edit#slide=id.g268764217b2_0_0



-----------------------------------------------------
Storage, How Many Disks? ,Partition Rebalance
-----------------------------------------------------


https://docs.google.com/presentation/d/1pMTUxbKQqLo9Dq_oqL-ZSaXo8PU1cQPndUyQwGgox3w/edit#slide=id.g268764217b2_0_105



-----------------------------------------------------
Broker's Listeners
-----------------------------------------------------

 Types of listeners

  - PLAINTEXT
  - TLS
  - EXTERNAL
  - LOADBALANCER
  - NODEPORT
  - ROUTE
  - INGRESS

  When to use which listener type?
  - PLAINTEXT: for internal communication
  - TLS: for external communication
  - EXTERNAL: for external communication 
  - LOADBALANCER: for external communication 
  - NODEPORT: for external communication 
  - ROUTE: for external communication (OpenShift)
  - INGRESS: for external communication (Kubernetes)


-----------------------------------------------------
Create truststore from CA certificate
-----------------------------------------------------  

./docs/truststore.md

-----------------------------------------------------
Test with Kafka Client ( Producer  ) ( java based)
-----------------------------------------------------  

🤚





kubectl get nodes --show-labels
kubectl apply -f ./kafka.yaml -n kafka
kubectl get pod -o wide -n kafka
kubectl delete -f ./kafka.yaml -n kafka




