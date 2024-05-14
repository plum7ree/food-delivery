import json
from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk

# JSON 파일 읽기
with open('../restaurant_data.json', 'r') as f:
    restaurant_data = json.load(f)

# Elasticsearch 클라이언트 생성
es = Elasticsearch(['http://localhost:9200'])

# 인덱스 생성
index_name = 'restaurants'
if not es.indices.exists(index=index_name):
    es.indices.create(index=index_name)

# 데이터 인덱싱
def generate_actions():
    for restaurant in restaurant_data['menuDtoList']:
        yield {
            '_index': index_name,
            '_id': restaurant['id'],
            '_source': restaurant
        }

bulk(es, generate_actions())