import json
from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk, streaming_bulk
from elasticsearch_dsl import Search, Q


LOCAL_URL = 'http://localhost:9200'
URL = LOCAL_URL


DATA_LOAD_FLAG= True
if DATA_LOAD_FLAG:
    # JSON 파일 읽기
    with open('./web_crawled_restaurant_data.json', 'r') as f:
        restaurant_data = json.load(f)

    # Elasticsearch 클라이언트 생성
    es = Elasticsearch([URL])

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
    try:
        # Streaming bulk operation
        for ok, action in streaming_bulk(es, generate_actions(), chunk_size=100, raise_on_error=False):
            if not ok:
                print(f'Failed to index document: {action}')
    except Exception as e:
        print(f'Streaming bulk failed error: {e}')


def search_restaurants(text):
    es = Elasticsearch([URL])
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