commands

```shell

helm dep update infra-prod-env
```

```shell

helm dep build infra-prod-env


```

```shell

helm template infra-prod-env

```

```shell

helm install infra-release infra-prod-env

```

```shell

kubectl get pv
```

- ```
    # available -> bound 상태 되어야함.
    NAME         CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                 STORAGECLASS   VOLUMEATTRIBUTESCLASS   REASON   AGE
    user-db-pv   10Gi       RWO            Retain           Bound    default/user-db-pvc   default        <unset>    
    ```

```shell

kubectl get pvc 
```

- ```
    #pending -> bound 되어야함
    NAME          STATUS   VOLUME       CAPACITY   ACCESS MODES   STORAGECLASS   VOLUMEATTRIBUTESCLASS   AGE
    user-db-pvc   Bound    user-db-pv   10Gi       RWO            default        <unset>                 71s
  ```

```shell

kubectl get pods

```

```shell

kubectl describe [pod-name]
kubectl logs user-db-deployment-5df975494c-29k2t


```

```shell

 helm uninstall infra-release

```