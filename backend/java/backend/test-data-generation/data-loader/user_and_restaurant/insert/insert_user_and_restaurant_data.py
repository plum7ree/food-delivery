import json
import psycopg2
import uuid
from datetime import datetime
import random

from pathlib import Path

# TODO batch

DB_HOST = 'localhost'
DB_NAME = 'postgres'
DB_USER = 'postgres'
DB_PASSWORD = 'admin'
DB_PORT = '5432'


# 데이터베이스 연결 설정
def setup_connection():
   conn = psycopg2.connect(
      host=DB_HOST,
      port=DB_PORT,
      database=DB_NAME,
      user=DB_USER,
      password=DB_PASSWORD
   )
   return conn


# JSON 파일 읽기
def read_json_file(file_path):
   with open(file_path, 'r', encoding='utf-8') as file:
      return json.load(file)


# Account 데이터 삽입
def insert_account(cursor, account_node):
   sql = """
    INSERT INTO user_schema.account (id, username, password, email, profile_pic_url, role, created_at, updated_at)
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
    """
   cursor.execute(sql, (
      str(uuid.UUID(account_node['id'])),
      account_node['username'],
      account_node['password'],
      account_node['email'],
      account_node.get('profile_pic_url'),
      account_node.get('role', 'USER'),  # 기본값으로 "USER" 설정
      account_node.get('created_at'),
      account_node.get('updated_at')
   ))


# Restaurant 데이터 삽입
def insert_restaurant(cursor, restaurant_node):
   sql = """
    INSERT INTO user_schema.restaurant (id, account_id, name, type, open_time, close_time, picture_url1, picture_url2, picture_url3, created_at, updated_at)
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s,%s, %s, %s)
    """
   cursor.execute(sql, (
      str(uuid.UUID(restaurant_node['id'])),
      str(uuid.UUID(restaurant_node['userId'])),
      restaurant_node['name'],
      restaurant_node['type'],
      datetime.strptime(restaurant_node['openTime'], "%H:%M:%S").time(),
      datetime.strptime(restaurant_node['closeTime'], "%H:%M:%S").time(),
      restaurant_node['pictureUrl1'],
      restaurant_node['pictureUrl2'],
      restaurant_node['pictureUrl3'],
      restaurant_node['created_at'],
      restaurant_node['updated_at']
   ))

   for menu_node in restaurant_node['menuDtoList']:
      insert_menu(cursor, menu_node, restaurant_node['id'])


# Menu 데이터 삽입
def insert_menu(cursor, menu_node, restaurant_id):
   sql = """
    INSERT INTO user_schema.menu (id, name, description, picture_url, price, currency, restaurant_id, created_at, updated_at)
    VALUES (%s, %s, %s, %s, %s, %s, %s, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    """
   menu_id = uuid.uuid4()
   cursor.execute(sql, (
      str(menu_id),
      menu_node['name'],
      menu_node['description'],
      menu_node['pictureUrl'],
      menu_node['price'],
      "KRW",
      str(uuid.UUID(restaurant_id))
   ))

   for option_group_node in menu_node['optionGroupDtoList']:
      insert_option_group(cursor, option_group_node, menu_id)


# Option Group 데이터 삽입
def insert_option_group(cursor, option_group_node, menu_id):
   sql = """
    INSERT INTO user_schema.option_group (id, description, max_select_number, is_necessary, menu_id)
    VALUES (%s, %s, %s, %s, %s)
    """
   option_group_id = uuid.uuid4()
   cursor.execute(sql, (
      str(option_group_id),
      option_group_node['description'],
      option_group_node['maxSelectNumber'],
      option_group_node['necessary'],
      str(menu_id)
   ))

   for option_node in option_group_node['optionDtoList']:
      insert_option(cursor, option_node, option_group_id)


# Option 데이터 삽입
def insert_option(cursor, option_node, option_group_id):
   sql = """
    INSERT INTO user_schema.option (id, name, cost, currency, option_group_id)
    VALUES (%s, %s, %s, %s, %s)
    """
   cursor.execute(sql, (
      str(uuid.UUID(option_node['id'])),
      option_node['name'],
      option_node['cost'],
      "KRW",
      str(option_group_id)
   ))


def insert_address(cursor):
   # 서울 지역 위경도 범위 설정
   min_lat = 37.4842
   max_lat = 37.5784
   min_lon = 126.9255
   max_lon = 127.0842

   # 사용자 테이블에서 사용자 ID를 가져옴
   cursor.execute("SELECT id FROM user_schema.account")
   user_ids = cursor.fetchall()

   # 각 사용자에 대해 랜덤한 위경도 생성 및 주소 레코드 삽입
   for user_id in user_ids:
      lat = min_lat + (max_lat - min_lat) * random.random()
      lon = min_lon + (max_lon - min_lon) * random.random()

      # 주소 테이블에 데이터 삽입
      cursor.execute("""
            INSERT INTO user_schema.address (id, user_id, city, street, postal_code, lat, lon)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """, (str(uuid.uuid4()), user_id[0], '', '', '', lat, lon))


# 전체 Account 수 출력
def count_and_print_total_accounts(cursor):
   sql = "SELECT COUNT(*) FROM user_schema.account"
   cursor.execute(sql)
   total_accounts = cursor.fetchone()[0]
   print(f"Total number of accounts inserted: {total_accounts}")


# 메인 함수
def main():
   conn = setup_connection()
   cursor = conn.cursor()

   try:
      # Account 데이터 삽입
      user_list = read_json_file("../generate_json/user_list.json")
      for account_node in user_list:
         insert_account(cursor, account_node)

      # Restaurant 데이터 삽입
      restaurant_list = read_json_file("../generate_json/web_crawled_restaurant_data.json")
      for restaurant_node in restaurant_list:
         insert_restaurant(cursor, restaurant_node)

      insert_address(cursor)

      # 결과 출력
      count_and_print_total_accounts(cursor)

      conn.commit()  # 변경사항 커밋
   except Exception as e:
      conn.rollback()  # 오류 발생 시 롤백
      print(f"An error occurred: {e}")
   finally:
      cursor.close()
      conn.close()


if __name__ == "__main__":
   main()
