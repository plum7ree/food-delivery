//package com.example.route.service;
//
//
//import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
//import org.openstreetmap.osmosis.core.domain.v0_6.Node;
//import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
//import org.openstreetmap.osmosis.core.task.v0_6.Sink;
//import org.openstreetmap.osmosis.xml.common.CompressionMethod;
//import org.openstreetmap.osmosis.xml.v0_6.XmlReader;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class OsmToAddress implements Sink {
//    private List<Address> addresses;
//
//    public OsmToAddress() {
//        addresses = new ArrayList<>();
//    }
//
//    @Override
//    public void process(EntityContainer entityContainer) {
//        if (entityContainer instanceof Node) {
//            Node node = (Node) entityContainer.getEntity();
//            if (node.getTags().containsKey("addr:housenumber") && node.getTags().containsKey("addr:street")) {
//                Address address = new Address();
//                address.setHousenumber(node.getTags().get("addr:housenumber"));
//                address.setStreet(node.getTags().get("addr:street"));
//                address.setLat(node.getLatitude());
//                address.setLon(node.getLongitude());
//                addresses.add(address);
//            }
//        }
//    }
//
//    @Override
//    public void initialize(Map<String, Object> map) {
//        // 초기화 작업 (필요한 경우)
//    }
//
//    @Override
//    public void complete() {
//        // 완료 작업 (필요한 경우)
//    }
//
//    @Override
//    public void close() {
//        // 종료 작업 (필요한 경우)
//    }
//
//    public List<Address> getAddresses() {
//        return addresses;
//    }
//
//    public static void main(String[] args) {
//        File osmFile = new File("path/to/your/osm/file.osm");
//        AddressExtractor extractor = new AddressExtractor();
//
//        RunnableSource reader = new XmlReader(osmFile, false, CompressionMethod.None);
//        reader.setSink(extractor);
//        reader.run();
//
//        List<Address> addresses = extractor.getAddresses();
//        for (Address address : addresses) {
//            System.out.println(address);
//        }
//    }
//
//    @Override
//    public void initialize(Map<String, Object> map) {
//
//    }
//}
//
//class Address {
//    private String housenumber;
//    private String street;
//    private double lat;
//    private double lon;
//
//    // Getter 및 Setter 메서드 (생략)
//}
//
//
//
//// Elasticsearch 클라이언트 생성
//RestHighLevelClient client = new RestHighLevelClient(
//        RestClient.builder(new HttpHost("localhost", 9200, "http")));
//
//// 검색 요청 생성
//SearchRequest searchRequest = new SearchRequest("addresses");
//SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//// 검색 쿼리 설정
//String searchAddress = "검색할 주소";
//String searchHousenumber = "검색할 번지수";
//
//BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//boolQuery.must(QueryBuilders.matchQuery("street", searchAddress));
//boolQuery.should(QueryBuilders.matchQuery("housenumber", searchHousenumber));
//
//searchSourceBuilder.query(boolQuery);
//searchSourceBuilder.fetchSource(new String[]{"housenumber", "street", "location", "poi_name"}, null);
//
//searchRequest.source(searchSourceBuilder);
//
//// 검색 실행
//SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//// 검색 결과 처리
//SearchHits hits = searchResponse.getHits();
//for (SearchHit hit : hits.getHits()) {
//    String housenumber = hit.getSourceAsMap().get("housenumber").toString();
//    String street = hit.getSourceAsMap().get("street").toString();
//    Map<String, Double> location = (Map<String, Double>) hit.getSourceAsMap().get("location");
//    String poiName = hit.getSourceAsMap().get("poi_name").toString();
//
//    // 검색 결과 사용
//    System.out.println("번지수: " + housenumber);
//    System.out.println("도로명: " + street);
//    System.out.println("위도: " + location.get("lat"));
//    System.out.println("경도: " + location.get("lon"));
//    System.out.println("POI 이름: " + poiName);
//}
//
//// Elasticsearch 클라이언트 종료
//client.close();