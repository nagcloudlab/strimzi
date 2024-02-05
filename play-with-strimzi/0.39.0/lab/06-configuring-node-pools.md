
Create a new node in the node pool.

```bash
kubectl scale kafkanodepool pool-a --replicas=4 -n kafka
kubectl get pods -n kafka
```

Reassign the partitions after increasing the number of nodes in the node pool.
After scaling up a node pool, you can use the Cruise Control add-brokers mode to move partition replicas from existing brokers to the newly added brokers. This mode is useful when you want to add new brokers to the cluster and balance the partition replicas across the brokers.




Remove a node from the node pool.

Reassign the partitions before decreasing the number of nodes in the node pool.
Before scaling down a node pool, you can use the Cruise Control remove-brokers mode to move partition replicas off the brokers that are going to be removed.

```bash
kubectl scale kafkanodepool pool-a --replicas=3 -n kafka
kubectl get pods -n kafka
```



Moving nodes between node pools

In this procedure, we start with two node pools:
pool-a with three replicas is the target node pool
pool-b with four replicas is the source node pool
We scale up pool-a, and reassign partitions and scale down pool-b, which results in the following:
pool-a with four replicas
pool-b with three replicas


Create a new node in the target node pool.
For example, node pool pool-a has three replicas. We add a node by increasing the number of replicas:

```bash
kubectl scale kafkanodepool pool-a --replicas=4 -n kafka
kubectl get pods -n kafka
```

Reassign the partitions from the old node to the new node.
Before scaling down the source node pool, you can use the Cruise Control remove-brokers mode to move partition replicas off the brokers that are going to be removed.

```bash
kubectl scale kafkanodepool pool-b --replicas=2
kubectl get pods -n kafka
```


Managing storage using node pools
https://strimzi.io/docs/operators/latest/deploying#proc-managing-storage-node-pools-str


Managing storage affinity using node pools
