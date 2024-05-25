import random
from sqlalchemy import create_engine, Column, Integer, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

# Database connection details
db_url = 'mysql://username:password@host:port/database'
engine = create_engine(db_url)
Session = sessionmaker(bind=engine)

# Define the SQLAlchemy model
Base = declarative_base()


class Restaurant(Base):
    __tablename__ = 'restaurants'

    id = Column(Integer, primary_key=True)
    name = Column(String(100))
    type = Column(String(50))
    open_time = Column(String(8))
    close_time = Column(String(8))


class Menu(Base):
    __tablename__ = 'menus'

    id = Column(Integer, primary_key=True)
    restaurant_id = Column(Integer)
    name = Column(String(100))
    price = Column(Integer)


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


def save_restaurant_data(restaurant_data):
    session = Session()

    # Save restaurant data
    restaurant = Restaurant(
        name=restaurant_data['name'],
        type=restaurant_data['type'],
        open_time=restaurant_data['openTime'],
        close_time=restaurant_data['closeTime']
    )
    session.add(restaurant)
    session.commit()

    # Save menu data
    for menu in restaurant_data['menuDtoList']:
        menu_item = Menu(
            restaurant_id=restaurant.id,
            name=menu['name'],
            price=menu['price']
        )
        session.add(menu_item)
    session.commit()

    session.close()


# Generate and save restaurant data
restaurant_data = generate_restaurant_data()
save_restaurant_data(restaurant_data)
