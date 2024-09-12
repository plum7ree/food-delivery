



from copy import deepcopy
N = 9
f = [0] * (N+1) # dp table

f[1] = 1 # 1원 짜리 1개
f[2] = 2 # 2 원을 만족하기 위해서는 1원 짜리 2개 필요함.
f[3] = 1 # 3원 동전 1개
f[4] = 1 # 4원 동전 1개


coins_list = [[1],[1,1],[3],[4]]

# 동전 추적까지 하려면 ?
for i in range(5, N+1):
  a = f[i-1]
  b = f[i-3]
  c = f[i-4]
  v_min = min(a,b,c)
  if v_min == a:
    coins = deepcopy(coins_list[-1])
    coins.append(1)
    coins_list.append(coins)
  elif v_min == b:
    coins = deepcopy(coins_list[-3])
    coins.append(3)
    coins_list.append(coins)
  elif v_min == c:
    coins = deepcopy(coins_list[-4])
    coins.append(4)
    coins_list.append(coins)
  f[i] = v_min + 1

print(f[N])
print(coins_list[-1])