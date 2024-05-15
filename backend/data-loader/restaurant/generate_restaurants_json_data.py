from collections import deque

import networkx
from selenium import webdriver
from bs4 import BeautifulSoup
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
    # "https://www.burgerking.co.kr/#/home"
]
SEARCH_PAGE_LIST = [
    "https://www.mcdonalds.co.kr/kor/menu/list.do"
]

G = nx.DiGraph()

G.add_node(0, tag='div', request_type=RequestTypeEnum.FIND_ALL, kwargs={ "class_" :"thum"})
G.add_node(1, tag='img', request_type=RequestTypeEnum.FIND)
G.add_node(2, tag='src', request_type=RequestTypeEnum.GET)
G.add_edges_from([(0, 1), (1, 2)])

print(G.nodes[1])

# strategy_list = [["prd_img",]]
#


def topological_crawl(G: networkx.DiGraph, soup):
    """

    :param G: Graph of HtmlTagNode
    :param soup:
    :return:

    """
    results = {}
    # { HtmlTagNode.id : inward_degree }
    node_id_to_degree_map = {node: G.in_degree(node) for node in G.nodes}
    # 1. put node degree starts with 0 or 1 (toppest level)
    html_tag_node_stack = deque()
    result_html_element_stack = deque()
    for node, degree in node_id_to_degree_map.items():
        if degree == 0:
            # { node, G.nodes[node] } =  { 0: {'tag':'div', 'request_type' : ...} }
            html_tag_node_stack.append((node, G.nodes[node])) #
    print(html_tag_node_stack)
    while len(html_tag_node_stack) > 0:
        node, attrs = html_tag_node_stack.pop()

        # result html. search from here
        scoped_html = ''
        if not len(result_html_element_stack):
            scoped_html = soup
        else:
            n_neighbors_left, scoped_html = result_html_element_stack[-1]
            if n_neighbors_left == 0:
                result_html_element_stack.pop()
                n_neighbors_left, scoped_html = result_html_element_stack[-1]
            else:
                result_html_element_stack[-1] = (n_neighbors_left - 1, scoped_html)

        result = str()
        if attrs['request_type'] == RequestTypeEnum.FIND_ALL:
            result = scoped_html.find_all(attrs['tag'], **attrs['kwargs'])
        if attrs['request_type'] == RequestTypeEnum.FIND:
            result = scoped_html.find(attrs['tag'], **attrs['kwargs'])
        if attrs['request_type'] == RequestTypeEnum.GET:
            result = scoped_html.get(attrs['tag'], **attrs['kwargs'])


        neighbors = list(G.neighbors(node)) # dict_keyiterator -> list
        result_html_element_stack.append((len(neighbors), result))
        html_tag_node_stack.append((neighbor, G.nodes[neighbor]) for neighbor in neighbors)


result_get_list = list()

# 웹페이지 열기
for idx, SEARCH_PAGE in enumerate(SEARCH_PAGE_LIST):
    driver.get(SEARCH_PAGE)
    html = driver.page_source
    soup = BeautifulSoup(html, "html.parser")

    topological_crawl(G, soup)

    print(result_get_list)

# 웹 드라이버 종료
driver.quit()
