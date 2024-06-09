from elasticsearch import Elasticsearch, helpers
import math

es = Elasticsearch(['http://localhost:9200'])

# 주소 인덱스의 총 문서 수 가져오기
address_count = es.count(index="address")["count"]
batch_size = 100

# 주소 인덱스에서 postal_code 가져오기
address_query = {
    "_source": ["postal_code"],
    "query": {
        "exists": {
            "field": "postal_code"
        }
    }
}

# osm 인덱스에서 postal_code와 osmid를 가져옴
osm_query = {
    "_source": ["postal_code", "osmid"],
    "query": {
        "exists": {
            "field": "postal_code"
        }
    }
}

# osm 인덱스에서 postal_code와 osmid를 가져와 딕셔너리로 변환
osm_codes = {}
#TODO this only updates 1000. do scroll api.
scan_result = helpers.scan(es, index="osm", query=osm_query, size=1000)
for hit in scan_result:
    print(hit)
    postal_code = hit["_source"]["postal_code"]
    osmid = hit["_source"]["osmid"]
    if postal_code in osm_codes:
        osm_codes[postal_code].append(osmid)
    else:
        osm_codes[postal_code] = [osmid]


# address 인덱스에 osmid 필드 추가
res = es.search(index="address", body=address_query, scroll='2m')
sid = res['_scroll_id']
total_hits = res['hits']['total']['value']
hits = res['hits']['hits']
print("total number of document (this is not per scroll) hits: {}".format(total_hits))
print('hits result: {}'.format(len(hits)))

while len(hits) > 0:
    actions = []
    for hit in hits:
        postal_code = hit["_source"]["postal_code"]
        if postal_code in osm_codes:
            osmids = osm_codes[postal_code]
            action = {
                "_op_type": "update",
                "_index": "address",
                "_id": hit["_id"],
                "script": {
                    "source": "ctx._source.osmids = params.osmids",
                    "params": {
                        "osmids": osmids
                    }
                }
            }
            actions.append(action)

    helpers.bulk(es, actions, index="address", refresh=True, request_timeout=30)
    print(f"Processed {len(actions)} documents")

    res = es.scroll(scroll_id=sid, scroll='2m')
    break

# 스크롤 컨텍스트 삭제
es.clear_scroll(scroll_id=sid)