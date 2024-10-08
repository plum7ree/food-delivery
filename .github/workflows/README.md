




local argo cd install
```shell
# ArgoCD 네임스페이스 생성
kubectl create namespace argocd

# ArgoCD 설치
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# 기본적으로 argocd-server 이름의 서비스(svc) 는 cluster Ip 이므로,
# 클러스터 내부에서만 접근할수 있다. 따라서 LB 로 바꿔줘야 외부에서 가능.
# ArgoCD 서버에 접속할 수 있도록 서비스 타입 변경 (선택 사항)
kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "LoadBalancer"}}'

# 클라우드 환경에서는 LB 타입으로 바꾸면 자동으로 외부 로드 밸런서 생성해주지만,
# 로컬해서는 생성 안된다. 따라서 post-forward 를 직접 해줘야함.
# localhost:8080 으로 로그인창 뜸.
kubectl port-forward svc/argocd-server -n argocd 8080:443

# 아디 admin 인데 비번 모르니깐 다음 명령어로 구함.
# pod 이름 알아내고
kubectl -n argocd get pods -l app.kubernetes.io/name=argocd-server -o name
# 출력된 pod 을 <argocd-server-pod-name> 에 붙여 넣으면 비번 나옴.
kubectl -n argocd exec -it <argocd-server-pod-name> -- argocd admin initial-password -n argocd
# admin, 비번 입력으로 로그인.

```

local argo cd
```shell
admin
rWeXqYjEpoHeLoCj
```



### local github action with "act"
prerequisite
- docker hub 에서 Personal access tokens 만든 다음 my.secrets 에 DOCKER_HUB_ACCESS_TOKEN (= DOCKER_PASSWORD) 으로 사용함. 


```shell
cd uber-msa

# 아래 에러 해결하려면 링크 참고
# Error: failed to start container: Error response from daemon: 
# error while creating mount source path 
# '/host_mnt/Users/.../.docker/run/docker.sock': mkdir /host_mnt/Users/.../.docker/run/docker.sock: operation not supported
# https://github.com/nektos/act/issues/2239#issuecomment-2189419148
ln -s /var/run/docker.sock ~/.docker/run/docker.sock
act --container-daemon-socket="unix:///var/run/docker.sock" --container-architecture linux/amd64 --secret-file .github/workflows/my.secrets --pull --reuse
# (or adding to ~/.actrc)


# 만약 용량 부족 에러 아래같이 뜨면. 도커 데스크톱. 디스크 이미지 크기 늘리거나 불필요한 것들 정리.
# [CI/CD Pipeline/build]   ❗  ::error::ENOSPC: no space left on device, write
docker system prune -a --volumes
# 결과: Total reclaimed space: 684GB (?!)
# 해결 --pull --reuse 옵션을 위의 act 에 붙인다.

```
