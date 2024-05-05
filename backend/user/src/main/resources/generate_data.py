import requests
import random
import os

# 레스토랑 이름 리스트
restaurant_names = [
    "Burger King", "Pizza Hut", "KFC", "Subway", "Domino's Pizza",
    "Taco Bell", "Wendy's", "McDonald's", "Starbucks", "Dunkin' Donuts",
    "Baskin-Robbins", "Panda Express", "Chipotle Mexican Grill", "Popeyes Louisiana Kitchen",
    "Chick-fil-A", "Five Guys", "In-N-Out Burger", "Shake Shack", "Carl's Jr.", "Dairy Queen"
]

# 레스토랑 타입 및 해당 타입의 메뉴 옵션
restaurant_types = {
    'BURGER': ['Hamburger', 'Cheeseburger', 'Bacon Burger', 'Double Burger', 'Veggie Burger'],
    'PIZZA': ['Pepperoni Pizza', 'Margherita Pizza', 'BBQ Chicken Pizza', 'Hawaiian Pizza', 'Veggie Supreme Pizza'],
    'CHICKEN': ['Original Recipe Chicken', 'Spicy Chicken', 'Grilled Chicken', 'Chicken Nuggets', 'Chicken Sandwich'],
    'ASIAN': ['Kung Pao Chicken', 'Mapo Tofu', 'Sushi Roll', 'Pad Thai', 'Bibimbap'],
    'MEXICAN': ['Burrito', 'Taco', 'Quesadilla', 'Enchilada', 'Nachos'],
    'DESERT': ['Ice Cream', 'Chocolate Cake', 'Cheesecake', 'Apple Pie', 'Tiramisu']
}

def generate_restaurant_data():
    restaurant_type = random.choice(list(restaurant_types.keys()))
    return {
        'name': random.choice(restaurant_names),
        'type': restaurant_type,
        'openTime': f'{random.randint(6, 12):02d}:00:00',
        'closeTime': f'{random.randint(18, 23):02d}:00:00',
        'menuDtoList': [
            {
                'name': menu,
                'price': random.randint(5, 20) * 1000
            }
            for menu in random.sample(restaurant_types[restaurant_type], random.randint(2, 5))
        ]
    }

# API 엔드포인트 URL
base_url = 'https://your-api-endpoint.com'

# 세션 생성 엔드포인트 호출
create_session_url = f'{base_url}/api/seller/create-session'
response = requests.post(create_session_url)
session_id = response.text

# Restaurant 데이터 생성
restaurant_data = generate_restaurant_data()
restaurant_data['sessionId'] = session_id

# 레스토랑 사진 업로드
restaurant_picture_url = f'{base_url}/api/seller/register/picture'
restaurant_picture_file = {'file': open('restaurant_picture.jpg', 'rb')}
restaurant_picture_data = {'sessionId': session_id, 'type': 'RESTAURANT'}
response = requests.post(restaurant_picture_url, files=restaurant_picture_file, data=restaurant_picture_data)
print(f'Restaurant picture uploaded with status code: {response.status_code}')

# 메뉴 사진 업로드
for menu in restaurant_data['menuDtoList']:
    menu_picture_url = f'{base_url}/api/seller/register/picture'
    menu_picture_file = {'file': open('menu_picture.jpg', 'rb')}
    menu_picture_data = {'sessionId': session_id, 'type': 'MENU'}
    response = requests.post(menu_picture_url, files=menu_picture_file, data=menu_picture_data)
    print(f'Menu picture uploaded with status code: {response.status_code}')

# Restaurant 등록 엔드포인트 호출
register_restaurant_url = f'{base_url}/api/seller/register/restaurant'
response = requests.post(register_restaurant_url, json=restaurant_data)
print(f'Restaurant registered with status code: {response.status_code}')