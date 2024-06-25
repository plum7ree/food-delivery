local function get_iso_date(days_offset)
    days_offset = days_offset or 0
    local t = os.time() + (days_offset * 24 * 60 * 60)
    return os.date('!%Y-%m-%dT%H:%M:%SZ', t)
end

redis.call('FLUSHALL')

local startDate = get_iso_date()
local endDate_1000000 = get_iso_date(30)
local endDate_2000000 = get_iso_date(15)

redis.call('HMSET', 'coupon_info_1000000', 'startDate', startDate, 'endDate', endDate_1000000, 'maxCount', 1000)
redis.call('HMSET', 'coupon_info_2000000', 'startDate', startDate, 'endDate', endDate_2000000, 'maxCount', 500)

redis.call('SET', 'coupon_count_1000000', 0)
redis.call('SET', 'coupon_count_2000000', 0)

redis.call('SET', 'user_coupon_issue_testUser_2000000', true)

return '초기 데이터가 Redis에 삽입되었습니다.'
