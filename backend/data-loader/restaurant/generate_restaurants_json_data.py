import sys

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.action_chains import ActionChains
from fake_useragent import UserAgent

# Selenium 드라이버 초기화
driver = webdriver.Chrome()  # 크롬 드라이버 사용


def mcdonals_extract():
    pass


def burgerking_extract():
    result = list()

    try:
        options = webdriver.ChromeOptions()
        ua = UserAgent()
        user_agent = ua.random
        options.add_argument(f'user-agent={user_agent}')
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
                EC.presence_of_all_elements_located((By.XPATH,  '//p[@class="tit"]/strong')) # 여기서 text 추출?
            )
            img_tags = WebDriverWait(driver, 10).until(
                EC.presence_of_all_elements_located((By.XPATH, '//div[@class="prd_img"]/span/img'))
            )
            for menu_name_tag, img_tag in zip(menu_name_tags, img_tags):
                img_links = img_tag.get_attribute('src')
                result.append({ 'name': menu_name_tag.text, 'pictureUrl': img_links})

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
    options = webdriver.ChromeOptions()
    ua = UserAgent()
    user_agent = ua.random
    options.add_argument(f'user-agent={user_agent}')
    driver = webdriver.Chrome(options=options)
    driver.get("https://order.wendys.com/category/100/hamburgers?lang=en_US")

    img_tags = driver.find_elements(By.XPATH,
                                    '//div[@role="presentation" and @class="image"]/img')  # 와퍼&주니어가 담긴 span 보다 상위의 a 태그 선택
    for img_tag in img_tags:
        print(img_tag.get_attribute('src'))


links = burgerking_extract()
print(links)

driver.quit()

# restaurant = {
#     name: '',
#     pictureUrl: '',
#     menuDtoList: [
#         {
#             name: '',
#             pictureUrl: ''
#         }
#     ]
# }
