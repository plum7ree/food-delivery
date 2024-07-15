import json
import uuid
import random

# RestaurantType 정의
RestaurantType = {
    'BURGER': 'BURGER',
    'PIZZA': 'PIZZA',
    'KOREAN': 'KOREAN',
    'CHINESE': 'CHINESE',
    'JAPANESE': 'JAPANESE',
    'MEXICAN': 'MEXICAN',
    'ITALIAN': 'ITALIAN',
    'AMERICAN': 'AMERICAN',
    'FUSION': 'FUSION',
    'SEARCH': 'SEARCH',
    'MISC': 'MISC',
}

# 레스토랑 이름 리스트
restaurant_type2names = {
    RestaurantType['BURGER']: ["McDonald's", "Burger King", "In-N-Out Burger", "Five Guys", "Shake Shack", "Smashburger", "Habit Burger Grill", "Fatburger", "Steak 'n Shake", "Jack in the Box"],
    RestaurantType['PIZZA']: ["Domino's", "Pizza Hut", "Papa John's", "Little Caesars", "Jet's Pizza", "Marco's Pizza", "Round Table Pizza", "Hungry Howie's", "Casey's", "Cici's Pizza"],
    RestaurantType['KOREAN']: ["Korean BBQ House", "Bibimbap", "Seoul Food", "Kimchi Palace", "Bulgogi Brothers", "Korean Grill", "Kimchi Taco", "Tofu House", "Seoul Garden", "Sura Korean Bistro"],
    RestaurantType['CHINESE']: ["Panda Express", "PF Chang's", "Sichuan House", "Dim Sum Palace", "Wok This Way", "Lotus Leaf", "Hunan Lion", "Szechuan Gourmet", "Chopsticks", "P.F. Chang's China Bistro"],
    RestaurantType['JAPANESE']: ["Sushi Sato", "Ramen Takumi", "Izakaya Akira", "Teppanyaki Grill", "Miso Hungry", "Momotaro", "Morimoto", "Nobu", "Zuma", "Katana"],
    RestaurantType['MEXICAN']: ["Taco Bell", "Chipotle", "Qdoba", "Baja Fresh", "Taqueria del Sol", "Abuelo's", "Casa Oaxaca", "Guacamole", "Mariscos Jaliscos", "Border Grill"],
    RestaurantType['ITALIAN']: ["Olive Garden", "Maggiano's", "Carrabba's", "Buca di Beppo", "Bertucci's", "Bravo Cucina Italiana", "Brio Tuscan Grille", "Carrabba's Italian Grill", "Gino's East", "Noodles & Company"],
    RestaurantType['AMERICAN']: ["Applebee's", "Chili's", "TGI Fridays", "Outback Steakhouse", "Cracker Barrel", "Ruby Tuesday", "Denny's", "Bonefish Grill", "LongHorn Steakhouse", "The Cheesecake Factory"],
    RestaurantType['FUSION']: ["The Cheesecake Factory", "P.F. Chang's", "Benihana", "The Keg Steakhouse", "The Capital Grille", "Yard House", "Fleming's Prime Steakhouse", "Seasons 52", "Eddie V's", "Morton's The Steakhouse"],
    RestaurantType['SEARCH']: ["Search Restaurant 1", "Search Restaurant 2", "Search Restaurant 3", "Search Restaurant 4", "Search Restaurant 5", "Search Restaurant 6", "Search Restaurant 7", "Search Restaurant 8", "Search Restaurant 9", "Search Restaurant 10"],
    RestaurantType['MISC']: ["Miscellaneous Restaurant 1", "Miscellaneous Restaurant 2", "Miscellaneous Restaurant 3", "Miscellaneous Restaurant 4", "Miscellaneous Restaurant 5", "Miscellaneous Restaurant 6", "Miscellaneous Restaurant 7", "Miscellaneous Restaurant 8", "Miscellaneous Restaurant 9", "Miscellaneous Restaurant 10"]
}

# 메뉴 이름 리스트
menu_type2names = {
    RestaurantType['BURGER']: ["Hamburger", "Cheeseburger", "Double-Double", "Big Mac", "Whopper", "Quarter Pounder", "Bacon Cheeseburger", "Mushroom Swiss Burger", "Patty Melt", "Veggie Burger"],
    RestaurantType['PIZZA']: ["Pepperoni Pizza", "Margherita Pizza", "Meat Lover's Pizza", "Vegetarian Pizza", "Hawaiian Pizza", "BBQ Chicken Pizza", "Supreme Pizza", "Cheese Pizza", "Spinach and Feta Pizza", "Pesto Pizza"],
    RestaurantType['KOREAN']: ["Bulgogi", "Bibimbap", "Japchae", "Kimchi Fried Rice", "Tteokbokki", "Kimbap", "Korean Fried Chicken", "Galbi", "Seafood Pancake", "Korean BBQ Platter"],
    RestaurantType['CHINESE']: ["Kung Pao Chicken", "Beef and Broccoli", "Fried Rice", "Egg Foo Young", "Wonton Soup", "Lo Mein", "Dim Sum", "Mapo Tofu", "Szechuan Beef", "Vegetable Lo Mein"],
    RestaurantType['JAPANESE']: ["Sushi Rolls", "Ramen", "Tempura", "Yakisoba", "Gyoza", "Teriyaki Chicken", "Katsu Curry", "Miso Soup", "Udon", "Sashimi"],
    RestaurantType['MEXICAN']: ["Tacos", "Burritos", "Enchiladas", "Quesadillas", "Fajitas", "Nachos", "Tamales", "Tostadas", "Carne Asada", "Chicken Mole"],
    RestaurantType['ITALIAN']: ["Spaghetti Bolognese", "Lasagna", "Chicken Parmesan", "Fettuccine Alfredo", "Margherita Pizza", "Eggplant Parmigiana", "Linguine with Clams", "Penne Arrabbiata", "Caprese Salad", "Tiramisu"],
    RestaurantType['AMERICAN']: ["Steak", "Ribs", "Fried Chicken", "Meatloaf", "Mac and Cheese", "Mashed Potatoes", "Burgers", "Hot Dogs", "Chicken Tenders", "Cobb Salad"],
    RestaurantType['FUSION']: ["Korean Fried Chicken", "Sushi Burrito", "Bulgogi Tacos", "Kung Pao Meatballs", "Ramen Burger", "Kimchi Fried Rice", "Sichuan Beef Noodles", "Thai Curry Mussels", "Poke Bowl", "Chicken Tikka Masala"],
    RestaurantType['SEARCH']: ["Search Menu 1", "Search Menu 2", "Search Menu 3", "Search Menu 4", "Search Menu 5", "Search Menu 6", "Search Menu 7", "Search Menu 8", "Search Menu 9", "Search Menu 10"],
    RestaurantType['MISC']: ["Misc Menu 1", "Misc Menu 2", "Misc Menu 3", "Misc Menu 4", "Misc Menu 5", "Misc Menu 6", "Misc Menu 7", "Misc Menu 8", "Misc Menu 9", "Misc Menu 10"]
}

# 메뉴 description 및 price
menu_type2descriptions = {
    RestaurantType['BURGER']: "A classic hamburger/cheeseburger with lettuce, tomato, onion, and spread on a freshly baked bun.",
    RestaurantType['PIZZA']: "A delicious pizza with a variety of toppings on a crispy crust.",
    RestaurantType['KOREAN']: "Authentic Korean dishes with bold flavors and traditional ingredients.",
    RestaurantType['CHINESE']: "Flavorful Chinese cuisine featuring a variety of meat, vegetable, and noodle dishes.",
    RestaurantType['JAPANESE']: "Freshly prepared sushi, ramen, and other Japanese specialties.",
    RestaurantType['MEXICAN']: "Mouth-watering Mexican favorites like tacos, burritos, and enchiladas.",
    RestaurantType['ITALIAN']: "Classic Italian dishes made with high-quality ingredients and family recipes.",
    RestaurantType['AMERICAN']: "Hearty American classics like steak, ribs, and fried chicken.",
    RestaurantType['FUSION']: "Innovative fusion dishes that blend flavors from different cuisines.",
    RestaurantType['SEARCH']: "Menu items for search-related restaurants.",
    RestaurantType['MISC']: "Menu items for miscellaneous restaurants."
}

menu_type2prices = {
    RestaurantType['BURGER']: random.randint(3000, 6000),
    RestaurantType['PIZZA']: random.randint(1500, 3500),
    RestaurantType['KOREAN']: random.randint(2000, 4000),
    RestaurantType['CHINESE']: random.randint(1800, 3800),
    RestaurantType['JAPANESE']: random.randint(2200, 4200),
    RestaurantType['MEXICAN']: random.randint(1800, 3800),
    RestaurantType['ITALIAN']: random.randint(2000, 4000),
    RestaurantType['AMERICAN']: random.randint(2500, 4500),
    RestaurantType['FUSION']: random.randint(2500, 4500),
    RestaurantType['SEARCH']: random.randint(1000, 3000),
    RestaurantType['MISC']: random.randint(1500, 3500)
}

def convert_to_dict(data_list):
    result_dict = {}
    for type, name, urls in data_list:
        if type not in result_dict:
            result_dict[type] = {}
        if name not in result_dict[type]:
            result_dict[type][name] = []
        result_dict[type][name].extend(urls)
    return result_dict