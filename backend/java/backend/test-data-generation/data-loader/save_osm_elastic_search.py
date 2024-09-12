import osmium as osm
import osmium.osm
from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk, BulkIndexError

INDEX_NAME = 'osm'
bulk_size = 100
DEBUG_COUNT = 2
ENABLE_ELASTIC_SEARCH_FLAG = True
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
        "name": {
            "type": "keyword"
        },
        "building_type": {
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

        if es:
            self.es.index(index=INDEX_NAME, body={
                'mapping': {
                    'properties': mapping
                }
            })

        self.data_count = 0

    def node(self, n):
        self.data_count += 1
        global DEBUG_COUNT
        '''
        tags: 
        - best ref: https://taginfo.openstreetmap.org/keys
        - https://taginfo.openstreetmap.org/ 
        - https://wiki.openstreetmap.org/wiki/Tags
        - geocoding: https://osm.kr/usage/, https://github.com/komoot/photon, 
        n.tags 종류들
        {tactile_paving=no,traffic_signals:minimap=no,ke...}
        {highway=crossing}
        {entrance=yes}
        {railway=level_crossing}
        {crossing:markings=yes,highway=crossing,crossing...}
        {addr:housenumber=24,addr:street=용마산로60가길,addr:c...}
        {ref=4,name:ja=建大入口駅 4番出口,name=건대입구역 4번출구,name:e...}
        {bus=yes,name=경찰청.동북아역사재단(중),public_transport=pl...}
        {name:ko=연천,name=연천,name:en=Yeoncheon,public_tra...}
        {local_ref=1,name:ko=소요산,ref=100,subway=yes,name...}
        ...
        '''

        '''
        name: 있는건 모두 사거리나 교차로 (node) 인듯함.
        way 는?
        '''

        from typing import List
        def has_tags(names: List[str], op: str, tags: osmium.osm.TagList) -> bool:
            """

            :param names:
            :param op:
                1. "and": all names should exist in tags.
                2. "or": at least one name should exist in tags
            :param tags:
            :return:
            """
            count_map = dict()
            for tag in tags:
                for name in names:
                    if tag.k.startswith(name):
                        if op == "or":
                            return True
                        elif op == "and":
                            # record if name exists.
                            count_map[name] = 1

            if op == "and":
                # if some missing names exist in tags, this will not equal.
                return len(count_map) == len(names)

            return False

        # if has_tags(["addr", "name"], "or", n.tags):
        if has_tags(["addr", "building", 'name'], "or", n.tags):
            # print(n.tags)
            address = {
                'housenumber': n.tags.get('addr:housenumber', ''),
                'street': n.tags.get('addr:street', ''),
                'city': n.tags.get('addr:city', ''),
                'postal_code': n.tags.get('addr:postcode', ''),
                'name': n.tags.get('name:ko', ''),
                'lat': n.location.lat,
                'lon': n.location.lon,
                'osmid': n.id
            }
            # if has_tags(["building=residential"], "or", n.tags):
            #     address['building_type'] = 'residential'
            doc = address
            # doc = {k: v for k, v in address.items() if v is not None and v != ''}
            # print(doc)
            self.row_list.append(doc)

    def update(self):
        batch = list()
        # print(self.data_count)
        # print(len(self.row_list))
        for row in self.row_list:

            action = {
                '_index': INDEX_NAME,
                '_source': row
            }
            batch.append(action)
            if len(batch) >= self.bulk_size:
                # print(batch)
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
    print("# document to update: {}".format(len(handler.row_list)))
    handler.update()


osm_file = 'seoul-non-military.osm.pbf'

# handler = AddressHandler(None)
# handler.apply_file(osm_file)
# print(handler.data_count)


if ENABLE_ELASTIC_SEARCH_FLAG:

    # Elasticsearch 연결 설정
    es = Elasticsearch(['http://localhost:9200'])

    if CREATE_INDEX_FLAG:
        create_index(osm_file, es)

    # search_results = es.search(index=INDEX_NAME, body={
    #     'query': {
    #         'bool': {
    #             'filter': [
    #                 {'exists': {'field': 'postal_code'}},
    #                 {'exists': {'field': 'housenumber'}}
    #             ]
    #         }
    #     },
    #     'from': 0,
    #     'size': 50
    # })

    search_results = es.search(index=INDEX_NAME, body={
        "query": {
            "multi_match": {
                "query": "을지로 4가",
                "fields": ["street", "city", "postal_code", "name"],
                "fuzziness": 0,
                "prefix_length": 0
            }
        },
        "from": 0,
        "size": 10
    })
    # [hit['_source'] for hit in search_results['hits']['hits']]
    import json
    pretty_json = json.dumps(search_results.body, indent=4, ensure_ascii=False)
    print(pretty_json)

