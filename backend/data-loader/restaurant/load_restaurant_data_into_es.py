import json
from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk
from elasticsearch_dsl import Search, Q

DATA_LOAD_FLAG= False
if DATA_LOAD_FLAG:
    # JSON 파일 읽기
    with open('./restaurant_data.json', 'r') as f:
        restaurant_data = json.load(f)

    # Elasticsearch 클라이언트 생성
    es = Elasticsearch(['http://localhost:9200'])

    # 인덱스 생성
    index_name = 'restaurants'
    if not es.indices.exists(index=index_name):
        es.indices.create(index=index_name)

    # 데이터 인덱싱
    def generate_actions():
        for restaurant in restaurant_data:
            yield {
                '_index': index_name,
                '_id': restaurant['id'],
                '_source': restaurant
            }

    bulk(es, generate_actions())


def search_restaurants(text):
    es = Elasticsearch(["http://localhost:9200"])
    s = Search(using=es, index="restaurants")

    # Searching for restaurant name
    name_query = Q("match", name=text)

    # Searching for menu name
    # menu_query = Q("nested", path="menuDtoList", query=Q("match", "menuDtoList.name", text))

    # Applying fuzzy search
    # query = (name_query | menu_query).fuzziness("AUTO")
    query = (name_query)

    response = s.query(query).execute()

    # Returning search results
    return [hit.to_dict() for hit in response]

res = search_restaurants("burger")
print(res)