import time
from collections import deque

import networkx
from selenium import webdriver
from bs4 import BeautifulSoup, ResultSet, PageElement
import networkx as nx

from enum import Enum


class RequestTypeEnum(Enum):
    FIND = 'find'
    FIND_ALL = 'find_all'
    GET = 'get'


# Selenium 드라이버 초기화
driver = webdriver.Chrome()  # 크롬 드라이버 사용

ORIGIN_LIST = [
    "https://www.mcdonalds.co.kr",
    "https://www.burgerking.co.kr/#/home"
]
SEARCH_PAGE_LIST = [
    "https://www.mcdonalds.co.kr/kor/menu/list.do",
    "https://www.burgerking.co.kr/#/menu/K100003/%EC%99%80%ED%8D%BC&%EC%A3%BC%EB%8B%88%EC%96%B4"
]

def mcdonals_extract():
    pass

def burgerking_extract():
    from selenium import webdriver
    from selenium.webdriver.common.by import By
    from selenium.webdriver.support.ui import WebDriverWait
    from selenium.webdriver.support import expected_conditions as EC
    from selenium.webdriver.common.action_chains import ActionChains
    from fake_useragent import UserAgent

    options = webdriver.ChromeOptions()
    ua = UserAgent()
    user_agent = ua.random
    options.add_argument(f'user-agent={user_agent}')
    driver = webdriver.Chrome(options=options)
    driver.get("https://www.burgerking.co.kr/")

    # 와퍼 메뉴 탭 클릭
    menu_tab = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CSS_SELECTOR, ".GNBWrap li:nth-child(1) button"))
    )
    actions = ActionChains(driver)
    actions.move_to_element(menu_tab).click().perform()

    # "submenu" 내부의 각 메뉴 항목 클릭
    submenu_items = driver.find_elements(By.XPATH, '//ul[@class="submenu"]/li/a/span[text()="와퍼&주니어"]/..') # 와퍼&주니어가 담긴 span 보다 상위의 a 태그 선택
    item = submenu_items[0]
    actions = ActionChains(driver)
    actions.move_to_element(item).click().perform()

    page_html = driver.page_source
    driver.quit()
    return page_html


burgerking_extract()

def mcdonalds_graph():
    G = nx.DiGraph()
    G.add_node(0, tag='div', request_type=RequestTypeEnum.FIND_ALL, kwargs={ "class_" :"thum"})
    G.add_node(1, tag='img', request_type=RequestTypeEnum.FIND)
    G.add_node(2, tag='src', request_type=RequestTypeEnum.GET)
    G.add_edges_from([(0, 1), (1, 2)])
    return G
def burgerking_graph():
    G = nx.DiGraph()
    G.add_node(0, tag='div', request_type=RequestTypeEnum.FIND_ALL, kwargs={"class_": "prd_img"})
    G.add_node(1, tag='img', request_type=RequestTypeEnum.FIND)
    G.add_node(2, tag='src', request_type=RequestTypeEnum.GET)
    G.add_edges_from([(0, 1), (1, 2)])
    return G

GRAPHS = [mcdonalds_graph(), burgerking_graph()]

def topological_dfs_crawl(G: networkx.DiGraph, soup):
    """

    :param G: Graph of HtmlTagNode
    :param soup:
    :return:

    """
    GET_RESULT = []
    # inward_degree
    node_id_to_degree_map = {node: G.in_degree(node) for node in G.nodes}
    # 1. find the root
    request_stack = deque()
    data_stack = deque()
    for node, degree in node_id_to_degree_map.items():
        if degree == 0:
            # { node, G.nodes[node] } =  { 0: {'tag':'div', 'request_type' : ...} }
            request_stack.append(node) #

    print(request_stack)
    while len(request_stack) > 0:
        node = request_stack.pop()
        attrs = G.nodes[node]
        # data is soup or ResultSet or PageElement or Value of the key, according to request_Type (find_all, find, get)
        data = None
        if not len(data_stack):
            data = soup
        else:
            n_neighbors_left, data = data_stack[-1]
            if n_neighbors_left == 0:
                data_stack.pop()
                n_neighbors_left, data = data_stack[-1]
            else:
                # when pop data_stack -> no children left for iteration.
                data_stack[-1] = (n_neighbors_left - 1, data)


        def _query(attrs, page_or_tag):
            query_result = None
            if attrs['request_type'] == RequestTypeEnum.FIND_ALL:
                # find_all() -> ResultSet
                if 'kwargs' in attrs:
                    query_result = page_or_tag.find_all(attrs['tag'], **attrs['kwargs'])
                else:
                    query_result = page_or_tag.find_all(attrs['tag'])
            elif attrs['request_type'] == RequestTypeEnum.FIND:
                # find -> PageElement
                if 'kwargs' in attrs:
                    query_result = page_or_tag.find(attrs['tag'], **attrs['kwargs'])
                else:
                    query_result = page_or_tag.find(attrs['tag'])
            elif attrs['request_type'] == RequestTypeEnum.GET:
                # Returns the value of the 'key' attribute for the tag
                if 'kwargs' in attrs:
                    query_result = page_or_tag.get(attrs['tag'], **attrs['kwargs'])
                else:
                    query_result = page_or_tag.get(attrs['tag'])
                GET_RESULT.append(query_result)
            return query_result

        query_result = None
        if isinstance(data, ResultSet) or isinstance(data, list):
            query_result = []
            for page in data:
                query_result.append(_query(attrs, page))
        elif isinstance(data, PageElement):
            query_result = _query(attrs, data)
        elif query_result is None:
            raise RuntimeError("query_result for G: {} is None. reconstruct graph!".format(G))
        else:
            query_result = _query(attrs, data)

        children = list(G.neighbors(node)) # dict_keyiterator -> list
        data_stack.append((len(children), query_result))

        for child in children:
            # print('child ', child)
            request_stack.append(child)
        # request_stack.append(child for child in children) # ERROR: this creates generator expression. not the value!
    return GET_RESULT

result_get_list = list()

# for idx, SEARCH_PAGE in enumerate(SEARCH_PAGE_LIST):
#     driver.get(SEARCH_PAGE)
#     html = driver.page_source
#     soup = BeautifulSoup(html, "html.parser")
#
#     print(soup.find_all('ul'))
#
#
#     G = GRAPHS[idx]
#     result_get_list = topological_dfs_crawl(G, soup)
#
#     print(result_get_list)

html = burgerking_extract()
print(html)
soup = BeautifulSoup(html, "html.parser")
G = burgerking_graph()
result_get_list = topological_dfs_crawl(G, soup)
print(result_get_list)




# driver.quit()


restaurant = {
    name: '',
    pictureUrl: '',
    menuDtoList: [
        {
            name: '',
            pictureUrl: ''
        }
    ]
}
