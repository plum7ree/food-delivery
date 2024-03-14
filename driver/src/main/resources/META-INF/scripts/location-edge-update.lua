local whereIsDriverNowKey = KEYS[1]
local currEdgeVisitKey = KEYS[2]
local oldEdgeVisitKey = KEYS[3]
local currentEdgeFieldName = ARGV[1]
local countFieldName = ARGV[2]
local currEdgeId = ARGV[3]
redis.log(redis.LOG_NOTICE, "locationEdgeKey: " .. whereIsDriverNowKey)
redis.log(redis.LOG_NOTICE, "locationEdgeFieldName: " .. currentEdgeFieldName)

local ok, err = redis.call('hset', tostring(whereIsDriverNowKey), tostring(currentEdgeFieldName), tostring(currEdgeId))
if not ok then
    redis.log(redis.LOG_NOTICE, "Error: "..err)
end

ok, err = redis.call('hincrby', oldEdgeVisitKey, countFieldName, tonumber(-1))
if not ok then
    redis.log(redis.LOG_NOTICE, "Error: "..err)
end
ok, err =  redis.call('hincrby', currEdgeVisitKey, countFieldName, tonumber(1))
if not ok then
    redis.log(redis.LOG_NOTICE, "Error: "..err)
end



return true