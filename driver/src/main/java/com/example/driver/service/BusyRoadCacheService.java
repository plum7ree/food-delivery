package com.example.driver.service;

import java.util.HashMap;

public class BusyRoadCacheService {

    private HashMap<String, Long> roadVisitMap;

    // redis 에서 sortedset 으로 매번 연산하기
    // vs 주기적으로 전체 데이터 가져와서 여기서 구축하기
    // vs 데이터 가져오기 말고 redis 에서 복사해서 zset 에 저장하기
    public void readFromHashTable() {

    }


}
