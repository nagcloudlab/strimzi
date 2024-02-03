


install azure cli
https://learn.microsoft.com/en-us/cli/azure/install-azure-cli


install kubectl with az aks install-cli
https://learn.microsoft.com/en-us/cli/azure/aks?view=azure-cli-latest#az-aks-install-cli



---
create azure-group and aks-cluster
---

```bash
az group delete -n aks-rg -y
az group create -l centralindia -n aks-rg
az aks create -g aks-rg -n aks --node-count 3 --generate-ssh-keys
```
