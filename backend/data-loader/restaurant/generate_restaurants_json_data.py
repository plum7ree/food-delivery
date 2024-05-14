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


class HtmlTagNode:
    def __init__(self, id, name, request_type: RequestTypeEnum, **kwargs):
        self.id = id
        self.name = name
        self.request_type = request_type
        self.kwargs = kwargs

    def __hash__(self):
        return hash(self.name)

    def __eq__(self, other: object):
        if not isinstance(other, HtmlTagNode):
            return False
        return self.id is other.id

    def __repr__(self):
        return f"HtmlTagNode(id='{self.id}' name='{self.name}', request_type='{self.request_type}' kwargs='{self.kwargs}')"


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

node0 = HtmlTagNode(0, 'div', request_type=RequestTypeEnum.FIND_ALL, class_="thum")
node1 = HtmlTagNode(1, 'img', request_type=RequestTypeEnum.FIND)
node2 = HtmlTagNode(2, 'src', request_type=RequestTypeEnum.GET)

G.add_edges_from([(node0, node1), (node1, node2)])


# strategy_list = [["prd_img",]]
#
# G.add_nodes_from([HtmlTagNode('prd_img'), HtmlTagNode'Bob', 'Charlie', 'David', 'Eve'])
# G.add_edges_from([('Alice', 'Bob'), ('Bob', 'Charlie'), ('Charlie', 'David'), ('David', 'Eve'), ('Eve', 'Alice')])
def crawl_page(soup, node):
    results = []
    if node.request_type == RequestTypeEnum.FIND:
        results.append(soup.find(node.name, **node.kwargs))
    elif node.request_type == RequestTypeEnum.FIND_ALL:
        results = soup.find_all(node.name, **node.kwargs)
    elif node.request_type == RequestTypeEnum.GET:
        results.append(soup.get(node.name))
    return results


def topological_crawl(G: networkx.DiGraph, soup):
    """

    :param G: Graph of HtmlTagNode
    :param soup:
    :return:

    """
    results = {}
    # { HtmlTagNode.id : inward_degree }
    node_id_to_degree_map = {node.id: G.in_degree(node) for node in G.nodes}
    # 1. put node degree starts with 0 or 1 (toppest level)
    html_tag_node_stack = deque()
    for k, v in node_id_to_degree_map.items():
        print(k, v)
        if v == 0:
            html_tag_node_stack.append(G.nodes.get(k)) # key 자체가 node 네 ... 어떻게 구하지 ?
    # html_tag_node_stack = deque([G.nodes.get(k) for k, v in id_to_degree_map.items() if v == 0])
    print(html_tag_node_stack)
    # while html_tag_node_stack:
    #     node = html_tag_node_stack.pop()
    #     nx.descendants(G, node.id)


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
