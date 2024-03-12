local driverId = KEYS[1]
local oldEdgeId = KEYS[2]
local currEdgeId = KEYS[3]
redis.call('hset', 'driver:edge:' .. driverId, currEdgeId)
--local current = redis.call('hget', KEYS[1])
--if current == ARGV[1]
--  then redis.call('SET', KEYS[1], ARGV[2])
--  return true
--end
--return false