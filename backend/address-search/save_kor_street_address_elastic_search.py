import csv
from elasticsearch import Elasticsearch
import elasticsearch_dsl


ELASTIC_PASSWORD = "6fX0s0E23oIeF4BGkYY9"
ELASTIC_USERNAME = "elastic"
ELASTIC_PATH = "http://localhost:9200"
INDEX_NAME = "nba_players"


# 1 도로명주소관리번호 26 문자 PK1
# 2 법정동코드 10 문자 PK2
# 3 시도명 40 문자
# 4 시군구명 40 문자
# 5 법정읍면동명 40 문자
# 6 법정리명 40 문자
# 7 산여부 1 문자 PK3 (0:대지, 1:산)
# 8 지번본번(번지) 4 숫자 PK4
# 9 지번부번(호) 4 숫자 PK5
# 10 도로명코드 12 문자 시군구코드(5) + 도로명번호(7)
# 11 지하여부 1 문자 0:지상, 1:지하, 2:공중
# 12 건물본번 5 숫자
# 13 건물부번 5 숫자
# 14 이동사유코드 2 문자 31: 신규, 34:수정, 63: 폐지
# ['11110101310001200009400000', '1111010100', '서울특별시', '종로구', '청운동', '', '0', '144', '3', '111103100012', '자하문로',
#  '0', '94', '0', '1111051500', '청운효자동', '03047', '', '20110729', '0', '', '', '', '']
# 1 Road Name Address Management Number 26 characters PK1
# 2 Legal Dong Code 10 characters PK2
# 3 Province Name 40 characters
# 4 City/County/District Name 40 characters
# 5 Legal Eup/Myeon/Dong Name 40 characters
# 6 Legal Ri Name 40 characters
# 7 Is Mountain 1 character PK3 (0: Land, 1: Mountain)
# 8 Parcel Main Number (Bun) 4 numbers PK4
# 9 Parcel Sub Number (Ho) 4 numbers PK5
# 10 Road Name Code 12 characters City/County/District Code(5) + Road Name Number(7)
# 11 Is Underground 1 character 0: Above Ground, 1: Underground, 2: Aerial
# 12 Building Main Number 5 numbers
# 13 Building Sub Number 5 numbers
# 14 Movement Reason Code 2 characters 31: New, 34: Modification, 63: Abolition
mapping = {
    "management_number": {
        "type": "text"
    },
    "serial_number": {
        "type": "text"
    },
    "province": {
        "type": "date"
    },
    "city": {
        "type": "keyword"
    },
    "team": {
        "type": "keyword"
    },
    "avg_scoring": {
        "type": "float"
    },
    "avg_rebound": {
        "type": "float"
    },
    "avg_assist": {
        "type": "float"
    },
    "country": {
        "type": "keyword"
    }}
# client.indices.create(index=INDEX_NAME,
#                           body={
#                               'mappings': {
#                                   'properties': mapping
#                               }
#                           })
def index_data(es, data):
    for row in data:
        print(row)
        doc = {
            'management_number': row[0],
            'serial_number': row[1],
            'province': row[2],
            'city': row[3],
            'town': row[4],
            'is_mountain': row[5],
            'main_code': row[6],
            'sub_code': row[7],
            'range_code': row[8],
            'road_name': row[9],
        }
        es.index(index=index_name, body=doc)

es = Elasticsearch(['http://localhost:9200'])
index_name = 'address_data'

if not es.indices.exists(index=index_name):
    es.indices.create(index=index_name)

# CSV 파일 경로
csv_file_path = 'rnaddrkor_seoul.csv'
row_list = list()
# CSV 파일 읽기
with open(csv_file_path, 'r', encoding='cp949') as file:
    csv_reader = csv.reader(file, delimiter='|')
    count = 10
    for row in csv_reader:
        print(row)
        if count < 0:
            break
        count -= 1
        row_list.append(row)
    # data = [decode_row(row) for row in csv_reader]

    # ['11110101310001200009400000', '1111010100', '서울특별시', '종로구', '청운동', '', '0', '144', '3', '111103100012', '자하문로',
    #  '0', '94', '0', '1111051500', '청운효자동', '03047', '', '20110729', '0', '', '', '', '']
    # ['11110101310001200009600000', '1111010100', '서울특별시', '종로구', '청운동', '', '0', '108', '14', '111103100012', '자하문로',
    #  '0', '96', '0', '1111051500', '청운효자동', '03047', '', '20110729', '1', '', '', '평안빌', '']
    # ['11110101310001200009800000', '1111010100', '서울특별시', '종로구', '청운동', '', '0', '108', '13', '111103100012', '자하문로',
    #  '0', '98', '0', '1111051500', '청운효자동', '03047', '', '20110729', '1', '', '', '청운빌라', '']
    # ['11110101310001200009900003', '1111010100', '서울특별시', '종로구', '청운동', '', '0', '134', '2', '111103100012', '자하문로',
    #  '0', '99', '3', '1111051500', '청운효자동', '03032', '', '20110729', '1', '', '풍림팍사이드빌라', '풍림팍사이드빌라', '']


# 데이터 인덱싱
index_data(es, row_list)