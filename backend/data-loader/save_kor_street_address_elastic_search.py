import csv
from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk, BulkIndexError

import elasticsearch_dsl

CREATE_INDEX_FLAG = False
print("flags. CREATE INDEX FLAG: {}".format(CREATE_INDEX_FLAG))

ELASTIC_PASSWORD = "6fX0s0E23oIeF4BGkYY9"
ELASTIC_USERNAME = "elastic"
ELASTIC_PATH = "http://localhost:9200"
INDEX_NAME = "address"
BULK_SIZE = 100

mapping = {
    # 1 도로명주소관리번호 26 문자 PK1
    "road_name_addr_mgt_number": {
        "type": "keyword"
    },
    # 2 법정동코드 10 문자
    "legal_dong_code": {
        "type": "keyword"
    },
    # 3 시도명 40 문자
    "province": {
        "type": "keyword"
    },
    # 4 시군구명 40 문자
    "city_county_district": {
        "type": "keyword"
    },
    # 5 법정읍면동명 40 문자
    "eup_myeon_dong": {
        "type": "keyword"
    },
    # 6 법정리명 40 문자
    "ri": {
        "type": "keyword"
    },
    # 7 산여부 1 문자 0:대지, 1:산
    "is_mountain": {
        "type": "boolean"
    },
    # 8 지번본번(번지) 4 숫자
    "parcel_main_number_bun": {
        "type": "integer"
    },
    # 9 지번부번(호) 4 숫자
    "parcel_sub_number_ho": {
        "type": "integer"
    },
    # 10 도로명코드 12 문자 PK2 (시군구코드(5) + 도로명번호(7))

    "road_name_code": {
        "type": "keyword"
    },
    # 11 도로명 80 문자
    "road_name": {
        "type": "text"
    },
    # 12 지하여부 1 문자 PK3 (0:지상, 1:지하, 2:공중)

    "is_underground": {
        "type": "integer"
    },
    # 13 건물본번 5 숫자 PK4

    "building_main_number": {
        "type": "integer"
    },
    # 14 건물부번 5 숫자 PK5

    "building_sub_number": {
        "type": "integer"
    },
    # 15 행정동코드 60 문자 참고용

    "adm_dong_code": {
        "type": "keyword"
    },
    # 16 행정동명 60 문자 참고용

    "adm_dong_name": {
        "type": "keyword"
    },
    # 17 기초구역번호 5 문자 우편번호

    "postal_code": {
        "type": "keyword"
    },
    # 18 이전도로명주소 400 문자

    "prev_road_name_addr": {
        "type": "text"
    },
    # 19 효력발생일 8 문자

    "effective_date": {
        "type": "date",
        "format": "yyyyMMdd"
    },
    # 20 공동주택구분 1 문자

    "aprt_complex_classification": {
        "type": "keyword"
    },
    # 21 이동사유코드 2 문자 31: 신규, 34:수정, 63: 폐지

    "movement_reason_code": {
        "type": "integer"
    },
    # 22 건축물대장건물명 400 문자

    "bld_name_in_bld_register": {
        "type": "text"
    },
    # 23 시군구용건물명 400 문자

    "bld_name_for_city_county_district": {
        "type": "text"
    },
    # 24 비고 200 문자

    "remarks": {
        "type": "text"
    }
}



def index_data(es, row_list):
    batch = []
    for row in row_list:
        doc = {
            'road_name_addr_mgt_number': row[0],
            'legal_dong_code': row[1],
            'province': row[2],
            'city_county_district': row[3],
            'eup_myeon_dong': row[4],
            'ri': row[5],
            'is_mountain': (row[6] == '1'),
            'parcel_main_number_bun': int(row[7]) if row[7] else None,
            'parcel_sub_number_ho': int(row[8]) if row[8] else None,
            'road_name_code': row[9],
            'road_name': row[10],
            'is_underground': int(row[11]),
            'building_main_number': int(row[12]) if row[12] else None,
            'building_sub_number': int(row[13]) if row[13] else None,
            'adm_dong_code': row[14],
            'adm_dong_name': row[15],
            'postal_code': row[16],
            'prev_road_name_addr': row[17],
            'effective_date': row[18],
            'aprt_complex_classification': row[19],
            'movement_reason_code': int(row[20]) if row[20] else None,
            'bld_name_in_bld_register': row[21],
            'bld_name_for_city_county_district': row[22],
            'remarks': row[23]
        }
        # remove field which value is None.
        doc = {k: v for k, v in doc.items() if v is not None and v != ''}
        # print(doc)
        action = {
            '_index': INDEX_NAME,
            '_source': doc
        }
        batch.append(action)
        if len(batch) > BULK_SIZE:
            try:
                bulk(es, batch)
            except BulkIndexError as e:
                print("Error indexing documents:")
                for error in e.errors:
                    print(error)
            batch = list()
        # es.index(index=INDEX_NAME, body=doc)


es = Elasticsearch(['http://localhost:9200'])

if CREATE_INDEX_FLAG:
    csv_file_path = 'rnaddrkor_seoul.csv'
    row_list = list()

    with open(csv_file_path, 'r', encoding='cp949') as file:
        csv_reader = csv.reader(file, delimiter='|')
        # count = 10
        for row in csv_reader:
            row_list.append(row)
        # ['11110101310001200009400000', '1111010100', '서울특별시', '종로구', '청운동', '', '0', '144', '3', '111103100012', '자하문로',
        #  '0', '94', '0', '1111051500', '청운효자동', '03047', '', '20110729', '0', '', '', '', '']
    if not es.indices.exists(index=INDEX_NAME):
        es.indices.create(index=INDEX_NAME,
                                  body={
                                      'mappings': {
                                          'properties': mapping
                                      }
                                  })
    # 데이터 인덱싱
    index_data(es, row_list)


# search_results = es.search(index=INDEX_NAME, body={
#         'query': {
#             'bool': {
#                 'must': [
#                     {'term': {'postal_code': '03032'}}
#                 ]
#             }
#         },
#         'from': 0,
#         'size': 50
#     })
#
# search_results = es.search(index=INDEX_NAME, body={
#         'query': {
#             'bool': {
#                 'must': [
#                     {'term': {'road_name': '자하문로'}}
#                 ]
#             }
#         },
#         'from': 0,
#         'size': 5
#     })

# search_results = es.search(index=INDEX_NAME, body={
#     "query": {
#         "multi_match": {
#             "query": "서울시 자하문로",
#             "fields": ["city_county_district", "eup_myeon_dong", "ri", "road_name"]
#         }
#     },
#     "from": 0,
#     "size": 10
# })

search_results = es.search(index=INDEX_NAME, body={
    "query": {
        "multi_match": {
            "query": "서울시 자하문로",
            "fields": ["city_county_district", "eup_myeon_dong", "ri", "road_name"],
            "fuzziness": 1,
            "prefix_length": 2
        }
    },
    "from": 0,
    "size": 10
})


# number of distinct postal_code


# number of distinct postal_code SQL version.
# SELECT postal_code, COUNT(*) AS count
# FROM table_name
# WHERE postal_code IS NOT NULL
# GROUP BY postal_code
# ORDER BY count DESC;


import json
pretty_json = json.dumps(search_results.body, indent=4,  ensure_ascii=False)

print(pretty_json)

# osm data test
