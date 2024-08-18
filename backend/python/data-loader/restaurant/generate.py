import concurrent.futures
import datetime
import json
import queue
import random
import sys
import time
import uuid
from concurrent.futures import ThreadPoolExecutor
from urllib.parse import urlparse, parse_qs

from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver import Keys
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.action_chains import ActionChains
from fake_useragent import UserAgent

from data_gen_utils import RestaurantType, restaurant_type2names, menu_type2names, menu_type2descriptions, \
    menu_type2prices, convert_to_dict

from faker import Faker


CRAWL_FLAG = True
GENERATE_FLAG = True


def mcdonals_extract():
    pass


def burgerking_extract():
    result = list()
    try:
        options = webdriver.ChromeOptions()
        ua = UserAgent()
        user_agent = ua.random
        options.add_argument(f'user-agent={user_agent}')
        options.add_argument('--headless=new')  # headless 모드 활성화
        driver = webdriver.Chrome(options=options)
        driver.get("https://www.burgerking.co.kr/")

        # "submenu" 내부의 각 메뉴 항목 클릭
        # 와퍼 메뉴 탭 클릭
        menu_tab = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.CSS_SELECTOR, ".GNBWrap li:nth-child(1) button"))
        )
        actions = ActionChains(driver)
        actions.move_to_element(menu_tab).perform()

        submenu_items = WebDriverWait(driver, 10).until(
            EC.presence_of_all_elements_located((By.XPATH, '//ul[@class="submenu"]/li/a'))
        )
        for item in submenu_items:
            actions = ActionChains(driver)
            actions.move_to_element(item).click().perform()

            menu_name_tags = WebDriverWait(driver, 10).until(
                EC.presence_of_all_elements_located((By.XPATH, '//p[@class="tit"]/strong'))  # 여기서 text 추출?
            )
            img_tags = WebDriverWait(driver, 10).until(
                EC.presence_of_all_elements_located((By.XPATH, '//div[@class="prd_img"]/span/img'))
            )
            for menu_name_tag, img_tag in zip(menu_name_tags, img_tags):
                img_links = img_tag.get_attribute('src')
                # result.append({'name': menu_name_tag.text, 'pictureUrl': img_links})
                result.append(img_links)
            menu_tab = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.CSS_SELECTOR, ".GNBWrap li:nth-child(1) button"))
            )
            actions = ActionChains(driver)
            actions.move_to_element(menu_tab).perform()
    except Exception as e:
        print(e, file=sys.stderr)

    # [{'name': '불맛 더블치즈팩', 'pictureUrl': 'https://d1cua0vf0mkpiy.cloudfront.net/images/menu/normal/2a508f65-534f-4d93-811b-60136a2b6346.png'}, ...]
    return result


def wendy_extract():
    result = list()
    try:
        options = webdriver.ChromeOptions()
        ua = UserAgent()
        user_agent = ua.random
        options.add_argument(f'user-agent={user_agent}')
        options.add_argument('--headless=new')  # headless 모드 활성화
        driver = webdriver.Chrome(options=options)
        driver.get("https://order.wendys.com/category/100/hamburgers?lang=en_US")

        img_tags = driver.find_elements(By.XPATH,
                                        '//div[@role="presentation" and @class="image"]/img')  # 와퍼&주니어가 담긴 span 보다 상위의 a 태그 선택
        for img_tag in img_tags:
            result.append(img_tag.get_attribute('src'))
    except Exception as e:
        print(e, file=sys.stderr)
    return result


def logos_extract(restaurant_type2names):
    '''

    :param restaurant_type2names:
    :return: [(type, url) ... ]
    '''
    print(f'{__name__} started')
    def _extract_logo(driver, type, name):
        img_url = str()
        try:

            search_query = f"{name}+brand+logo"
            search_url = f"https://www.google.com/search?q={search_query}&tbm=isch"
            driver.get(search_url)

            # Wait for the search results to load
            WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.XPATH, "//img[contains(@alt, 'logo') or contains(@alt, 'Logo')]")))

            soup = BeautifulSoup(driver.page_source, "lxml")
            # Find all image elements with "logo" or "Logo" in the alt attribute
            img_elements = soup.select("img[alt*='logo'], img[alt*='Logo']")

            # Extract the source URLs of the images
            for img_element in img_elements:
                _img_url = img_element.get("src")
                if _img_url:
                    img_url = _img_url
                    # print(img_url)
                    break
            time.sleep(0.1)  # Add a small delay to avoid overloading the server

        except Exception as e:
            print(f'type: {type}, name: {name} error: {e}', file=sys.stderr)
        finally:
            return type,name,img_url

    logoUrls = queue.Queue()
    task_queue = queue.Queue()  # thread-safe queue

    # 작업을 큐에 추가
    for type in restaurant_type2names:
        for name in restaurant_type2names[type]:
            task_queue.put((type, name))

    def _worker():
        # 각 워커마다 task queue 에서 type name 가져와서 작업 수행.
        options = webdriver.ChromeOptions()
        ua = UserAgent()
        user_agent = ua.random
        options.add_argument(f'user-agent={user_agent}')
        options.add_argument('--headless=new')  # headless 모드 활성화
        driver = webdriver.Chrome(options=options)
        while True:
            try:
                type, name = task_queue.get(timeout=1)
                print(f'task_queue size: {task_queue.qsize()} type: {type}, name: {name} starting')
                type_name_url = _extract_logo(driver, type, name)
                logoUrls.put(type_name_url)
            except queue.Empty:
                break
        driver.quit()
        task_queue.task_done()

    NUM_WORKERS = 10
    with ThreadPoolExecutor(max_workers=NUM_WORKERS) as executor:
        futures = []

        for _ in range(NUM_WORKERS):
            future = executor.submit(_worker)
            futures.append(future)

        for future in concurrent.futures.as_completed(futures):
            try:
                # future.result()
                future.done()
            except Exception as e:
                print(f"Error occurred: {e}")

    print(f'{__name__} done. total data:{logoUrls.qsize()}')

    return list(logoUrls.queue)


def menus_extract(menu_type2names):
    '''

    :param menu_type2names:
    :return: [(type, name, [url...]) ... ]
    '''
    print(f'{__name__} started')
    def _task(driver, type, name):
        menuUrls = list()
        try:
            type_cap = type.capitalize()  # BURGER -> Burger, burger -> Burger
            name_cap = name.capitalize()
            search_query = f"{type}+{name}+food+image"
            print(search_query)
            search_url = f"https://www.google.com/search?q={search_query}&tbm=isch"
            driver.get(search_url)

            # Wait for the search results to load
            WebDriverWait(driver, 10).until(EC.presence_of_element_located(
                (By.XPATH, f"//img[contains(@alt, '{type}') or contains(@alt, '{type_cap}') or contains(@alt, '{name}') or contains(@alt, '{name_cap}')]")))

            driver.find_elements(By.TAG_NAME, 'body')[0].send_keys(Keys.PAGE_DOWN)
            driver.find_elements(By.TAG_NAME, 'body')[0].send_keys(Keys.PAGE_DOWN)

            # Find all image elements with "logo" or "Logo" in the alt attribute
            img_elements = driver.find_elements(By.XPATH,
                                                f"//img[contains(@alt, '{type}') or contains(@alt, '{type_cap}') or contains(@alt, '{name}') or contains(@alt, '{name_cap}')]")

            print(img_elements)
            # Extract the source URLs of the images
            for img_element in img_elements:
                _img_url = img_element.get_attribute("src")
                if _img_url:
                    menuUrls.append(_img_url)
                    # print(_img_url)
            time.sleep(0.1)  # Add a small delay to avoid overloading the server

        except Exception as e:
            print(f'menu extract type: {type} error: {e}', file=sys.stderr)

        return (type, name, menuUrls)


    task_queue = queue.Queue()
    menuUrls = queue.Queue() # (type, name, [url...])

    for type, names in menu_type2names.items():
        for name in names:
            task_queue.put((type, name))
    def _worker():
        # 각 워커마다 task queue 에서 type name 가져와서 작업 수행.
        options = webdriver.ChromeOptions()
        ua = UserAgent()
        user_agent = ua.random
        options.add_argument(f'user-agent={user_agent}')
        options.add_argument('--headless=new')  # headless 모드 활성화
        driver = webdriver.Chrome(options=options)
        while True:
            try:
                type, name = task_queue.get(timeout=1)
                print(f'task_queue size: {task_queue.qsize()} type: {type}')
                type, name, menu_urls = _task(driver, type, name)
                menuUrls.put((type, name, menu_urls))
            except queue.Empty:
                break
        driver.quit()
        task_queue.task_done()

    NUM_WORKERS = 10
    with ThreadPoolExecutor(max_workers=NUM_WORKERS) as executor:
        futures = []

        for _ in range(NUM_WORKERS):
            future = executor.submit(_worker)
            futures.append(future)

        # 모든 작업이 완료될 때까지 대기
        for future in concurrent.futures.as_completed(futures):
            try:
                # future.result()
                future.done()
            except Exception as e:
                print(f"Error occurred: {e}")
        print(f"task_queue join done ")
    print(f'{__name__} done. total data:{menuUrls.qsize()}')
    return list(menuUrls.queue)


logoUrls = list()
menuUrls = list()
if CRAWL_FLAG:
    # pictureUrls.extend(burgerking_extract())
    # pictureUrls.extend(wendy_extract())
    # print(pictureUrls)
    logoUrls = logos_extract(restaurant_type2names)
    menuUrls = menus_extract(menu_type2names)
    # pictureUrls = menus_extract2(restaurant_type2names)

def generate_user(id, username, password, email, role):
    return {
        "id": id,
        "username": username,
        "password": password,
        "email": email,
        "role": role
    }

def generate_restaurant(name, type, pictureUrl, menus, userId=str(uuid.uuid4())):
    open_time = datetime.datetime(2023, 5, 13, 11, 0, 0)
    close_time = datetime.datetime(2023, 5, 13, 22, 0, 0)

    return {
        "id": str(uuid.uuid4()),
        "sessionId": str(uuid.uuid4()),
        "userId": userId,
        "name": name,
        "type": type,
        "rating": round(random.uniform(3.9, 4.9), 1),
        "openTime": open_time.strftime("%H:%M:%S"),
        "closeTime": close_time.strftime("%H:%M:%S"),
        "pictureUrl1": pictureUrl,
        "menuDtoList": menus
    }


def generate_menu(name, pictureUrl, options):
    return {
        "id": str(uuid.uuid4()),
        "name": name,
        "description": "A classic hamburger with lettuce, tomato, onion, and spread on a freshly baked bun",
        "price": 3500,
        "pictureUrl": pictureUrl,
        "optionGroupDtoList": [
            {
                "description": "Select options",
                "maxSelectNumber": 1,
                "selectedIndicesList": [],
                "optionDtoList": options,
                "necessary": False
            }
        ]
    }


def generate_options():
    return [
        {
            "id": str(uuid.uuid4()),
            "name": "Cheese",
            "cost": 500
        },
        {
            "id": str(uuid.uuid4()),
            "name": "Bacon",
            "cost": 1000
        },
        {
            "id": str(uuid.uuid4()),
            "name": "Pickles",
            "cost": 1000
        }
    ]


def generate_review(user_id, user_name, restaurant_id, menu_images):
    now = datetime.datetime.now()
    created_at = now - datetime.timedelta(days=random.randint(1, 30))  # 1~30일 전에 생성됨

    return {
        "id": str(uuid.uuid4()),
        "userId": user_id,
        "userName": user_name[:2] + "**",  # 이름의 첫 두 글자만 표시하고 나머지는 **로 마스킹
        "restaurantId": restaurant_id,
        "rating": round(random.uniform(1, 5), 1),  # 1.0에서 5.0 사이의 소수점 한 자리 rating
        "created_at": created_at.isoformat(),  # ISO 형식의 문자열로 변환
        "updated_at": now.isoformat(),  # 현재 시간을 업데이트 시간으로 사용
        "imageUrl": random.choice(menu_images) if menu_images else None,
        "comment": fake.paragraph()[:1000]  # 최대 1000자로 제한
    }

# 리뷰 생성 예시
def generate_reviews_for_restaurant(_restaurant, _users, num_reviews=10):
    _reviews = []
    menu_images = [
        "https://res.cloudinary.com/the-infatuation/image/upload/c_fill,w_1200,ar_4:3,g_center,f_auto/cms/7th_20Street_20Burger-Single_20Cheeseburger-Emily_20Schindler"
        for _ in range(len(_restaurant["menuDtoList"]))]

    # 랜덤하게 사용자 선택 (중복 없이)
    selected_users = random.sample(_users, num_reviews)

    for user in selected_users:
        _review = generate_review(
            user_id=user["id"],
            user_name=user["username"],
            restaurant_id=_restaurant["id"],
            menu_images=menu_images
        )
        _reviews.append(_review)

    return _reviews

if GENERATE_FLAG:
    print('Starting Generation Data')
    # example_usage.py
    pictureUrlIdx = 0
    logoUrlIdx = 0

    fake = Faker()
    # 레스토랑 타입별 이름 출력
    restaurantList = list()
    userList = list()

    menuTypeNameUrlsDict = convert_to_dict(menuUrls)
    # print(menuTypeNameUrlsDict)
    for val in logoUrls:
        type, name, logo_url = val
        menus = list()
        print(type, name, logo_url)
        for idx, menu_name in enumerate(menu_type2names[type]):
            print(idx, menu_name)
            description = menu_type2descriptions[type]
            price = menu_type2prices[type]
            options = generate_options()
            n_of_menu_urls = len(menuTypeNameUrlsDict[type][menu_name])
            if n_of_menu_urls > 0:
                random_menu_idx = random.randint(0, n_of_menu_urls - 1)
                menus.append(generate_menu(menu_name, menuTypeNameUrlsDict[type][menu_name][random_menu_idx], options))
            else:
                menus.append(generate_menu(menu_name, '', options))

        userId = fake.uuid4()
        userName = fake.user_name()
        userEmail = fake.email()

        userList.append(generate_user(userId, userName, str(), userEmail, str())) # one user per one restaurant)
        restaurantList.append(generate_restaurant(name, type, logo_url, menus, userId=userId))

    all_reviews = []
    for restaurant in restaurantList:
        reviews = generate_reviews_for_restaurant(restaurant, userList)
        all_reviews.extend(reviews)

    file_name = "web_crawled_logo_list.json"
    with open(file_name, "w+") as f:
        json.dump(logoUrls, f, indent=2)

    file_name = "web_crawled_menu_list.json"
    with open(file_name, "w+") as f:
        json.dump(menuTypeNameUrlsDict, f, indent=2)

    file_name = "user_list.json"
    with open(file_name, "w+") as f:
        json.dump(userList, f, indent=2)

    file_name = "web_crawled_restaurant_data.json"
    with open(file_name, "w+") as f:
        json.dump(restaurantList, f, indent=2)

    file_name = "generated_reviews.json"
    with open(file_name, "w+") as f:
        json.dump(all_reviews, f, indent=2)

    print(f"Restaurant data saved to '{file_name}'.")

print(f'Program Done')
