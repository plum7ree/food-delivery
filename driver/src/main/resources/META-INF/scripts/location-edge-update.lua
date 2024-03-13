local locationEdgeKey = KEYS[1]
local currEdgeVisitKey = KEYS[2]
local oldEdgeVisitKey = KEYS[3]
local locationEdgeFieldName = ARGV[1]
local edgeVisitFieldName = ARGV[2]
local driverId = ARGV[3]
local currEdgeId = ARGV[4]
local oldEdgeId = ARGV[5]
--redis.call('hset', 'driver:driver1', 'location:edge', '100001')
redis.log(redis.LOG_NOTICE, "locationEdgeKey: " .. locationEdgeKey)
redis.log(redis.LOG_NOTICE, "locationEdgeFieldName: " .. locationEdgeFieldName)
--redis.call('hset', locationEdgeKey, locationEdgeFieldName, currEdgeId)
redis.call('hset', tostring(locationEdgeKey), tostring(locationEdgeFieldName), tostring(currEdgeId))

return true
--local current = redis.call('hget', KEYS[1])
--if current == ARGV[1]
--  then redis.call('SET', KEYS[1], ARGV[2])
--  return true
--end
--return false