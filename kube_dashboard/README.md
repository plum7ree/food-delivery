after running `helm install uber-msa prod-env`

to see cluster with a dash UI, please run this


### dashboard 세팅
```shell

kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
kubectl proxy 
```

- link generate:  http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/.

### dashboard token 세팅
#### 1. service account 생성
```shell
cd kube_dashboard
kubectl apply -f ./dashboard-adminuser.yaml
```

#### 2. privileges 생성. ClusterRoleBinding
```shell
kubectl apply -f dashboard-rolebinding.yaml
kubectl -n kubernetes-dashboard create token admin-user
```
결과: 
```shell
eyJhb....매우긴 토근 생성...GciOiJS
```

### 종료
```shell
curl -o kubernetes-dashboard.yaml https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
kubectl delete -f kubernetes-dashboard.yaml
kubectl delete deployments --all --all-namespaces

```
