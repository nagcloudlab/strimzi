


install azure cli
https://learn.microsoft.com/en-us/cli/azure/install-azure-cli


install kubectl with az aks install-cli
https://learn.microsoft.com/en-us/cli/azure/aks?view=azure-cli-latest#az-aks-install-cli



---
create azure-group and aks-cluster
---

```bash
az group create -l centralindia -n nag-rg
az aks create -g nag-rg -n aks --node-count 1 --generate-ssh-keys
az group delete -n nag-rg -y
```











