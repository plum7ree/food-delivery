import osmium as osm
from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk, BulkIndexError

INDEX_NAME = 'osm'
bulk_size = 100
DEBUG_COUNT = 2
CREATE_INDEX_FLAG = True

# osm data tags
# 건물 관련 태그:
# building: 건물의 존재를 나타냅니다.
# building:use: 건물의 용도를 나타냅니다. (예: residential, commercial, industrial 등)
# building:levels: 건물의 층수를 나타냅니다.
# 도로 관련 태그:
# highway: 도로의 종류를 나타냅니다. (예: motorway, primary, residential 등)
# name: 도로의 이름을 나타냅니다.
# oneway: 일방통행 여부를 나타냅니다.
# maxspeed: 도로의 최대 속도 제한을 나타냅니다.
# 토지 이용 태그:
# landuse: 토지의 용도를 나타냅니다. (예: residential, commercial, industrial, agricultural 등)
# natural: 자연적인 특징을 나타냅니다. (예: wood, water, grassland 등)
# 편의 시설 태그:
# amenity: 편의 시설의 종류를 나타냅니다. (예: school, hospital, restaurant, bank 등)
# shop: 상점의 종류를 나타냅니다. (예: supermarket, bakery, clothing 등)
# 기타 태그:
# name: 지역, 건물, 시설 등의 이름을 나타냅니다.
# operator: 시설이나 서비스의 운영자를 나타냅니다.
# opening_hours: 시설의 운영 시간을 나타냅니다.

mapping = {
    "properties": {
        "housenumber": {
            "type": "keyword"
        },
        "street": {
            "type": "text"
        },
        "city": {
            "type": "keyword"
        },
        "postal_code": {
            "type": "keyword"
        },
        "lat": {
            "type": "float"
        },
        "lon": {
            "type": "float"
        },
        "osmid": {
            "type": "long"
        }
    }
}
class AddressHandler(osm.SimpleHandler):
    def __init__(self, es):
        osm.SimpleHandler.__init__(self)
        self.row_list = []
        self.es = es
        self.bulk_size = bulk_size

        self.es.index(index=INDEX_NAME, body={
            'mapping': {
                'properties': mapping
            }
        })

        self.data_count = 0

    def node(self, n):
        self.data_count += 1
        global DEBUG_COUNT
        if 'addr:postcode' in n.tags:
            address = {
                'housenumber': n.tags.get('addr:housenumber',''),
                'street': n.tags.get('addr:street',''),
                'city': n.tags.get('addr:city', ''),
                'postal_code': n.tags.get('addr:postcode', ''),
                'lat': n.location.lat,
                'lon': n.location.lon,
                'osmid': n.id
            }
            doc = {k: v for k, v in address.items() if v is not None and v != ''}
            # print(doc)
            self.row_list.append(doc)


    def update(self):
        batch = list()
        print(self.data_count)
        print(len(self.row_list))
        for row in self.row_list:

            action = {
                '_index': INDEX_NAME,
                '_source': row
            }
            batch.append(action)
            if len(batch) >= self.bulk_size:
                print(batch)
                try:
                    bulk(self.es, batch)
                except BulkIndexError as e:
                    print("Error indexing documents:")
                    for error in e.errors:
                        print(error)
                batch = list()


def create_index(osm_file, es):
    handler = AddressHandler(es)
    handler.apply_file(osm_file)
    handler.update()





# Elasticsearch 연결 설정
es = Elasticsearch(['http://localhost:9200'])


# 사용 예시
osm_file = 'seoul-non-military.osm.pbf'

if CREATE_INDEX_FLAG:
    create_index(osm_file, es)


search_results = es.search(index=INDEX_NAME, body={
        'query': {
            'bool': {
                'filter': [
                    {'exists': {'field': 'postal_code'}},
                    {'exists': {'field': 'housenumber'}}
                ]
            }
        },
        'from': 0,
        'size': 50
    })

# [hit['_source'] for hit in search_results['hits']['hits']]
print(search_results)