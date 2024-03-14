local whereIsDriverNowKey = KEYS[1]
local currEdgeVisitKey = KEYS[2]
local oldEdgeVisitKey = KEYS[3]
local currentEdgeFieldName = ARGV[1]
local edgeVisitFieldName = ARGV[2]
local driverId = ARGV[3]
local currEdgeId = ARGV[4]
local oldEdgeId = ARGV[5]
--redis.call('hset', 'driver:driver1', 'location:edge', '100001')
redis.log(redis.LOG_NOTICE, "locationEdgeKey: " .. whereIsDriverNowKey)
redis.log(redis.LOG_NOTICE, "locationEdgeFieldName: " .. currentEdgeFieldName)
--redis.call('hset', locationEdgeKey, locationEdgeFieldName, currEdgeId)
redis.call('hset', tostring(whereIsDriverNowKey), tostring(currentEdgeFieldName), tostring(currEdgeId))

return true
--local current = redis.call('hget', KEYS[1])
--if current == ARGV[1]
--  then redis.call('SET', KEYS[1], ARGV[2])
--  return true
--end
--return false