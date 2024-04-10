import osmium as osm
from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk


bulk_size = 100
DEBUG_COUNT = 2


class AddressHandler(osm.SimpleHandler):
    def __init__(self):
        osm.SimpleHandler.__init__(self)
        self.addresses = []
        self.es_client = es_client
        self.bulk_size = bulk_size

    def node(self, n):
        global DEBUG_COUNT
        if 'addr:housenumber' in n.tags and 'addr:street' in n.tags:
            address = {
                'housenumber': n.tags['addr:housenumber'],
                'street': n.tags['addr:street'],
                'city': n.tags.get('addr:city', ''),
                'postcode': n.tags.get('addr:postcode', ''),
                'lat': n.location.lat,
                'lon': n.location.lon,
                'osmid': n.id
            }
            self.addresses.append(address)
            if DEBUG_COUNT > 0:
                print(address)
                DEBUG_COUNT -= 1
            self.es_client.index(index='addresses', body=address)
            if len(self.addresses) >= self.bulk_size:
                self.bulk_update()

    def bulk_update(self):
        actions = [
            {
                '_index': 'addresses',
                '_source': address
            }
            for address in self.addresses
        ]
        bulk(self.es_client, actions)
        self.addresses = []



def search_address(osm_file, search_query):
    handler = AddressHandler()
    handler.apply_file(osm_file)

    # 남아있는 주소 정보 업데이트
    if handler.addresses:
        handler.bulk_update()

    search_results = es_client.search(index='addresses', body={
        'query': {
            'exists': {
                'field': 'postcode'
            }
        },
        'from': 0,
        'size': 50
    })

    return [hit['_source'] for hit in search_results['hits']['hits']]


# Elasticsearch 연결 설정
es_client = Elasticsearch(['http://localhost:9200'])

# 사용 예시
osm_file = 'seoul-non-military.osm.pbf'
search_query = '영등포구'

results = search_address(osm_file, search_query)





if results:
    for addr in results:
        print(f"주소: {addr['street']} {addr['housenumber']}, {addr['city']} {addr['postcode']}")
        print(f"위도: {addr['lat']}, 경도: {addr['lon']}")
        print("---")
else:
    print("검색 결과가 없습니다.")