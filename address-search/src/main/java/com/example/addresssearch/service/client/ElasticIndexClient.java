package com.example.addresssearch.service.client;

import com.example.addresssearch.data.AddressIndexModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "elastic-config.")
public class ElasticIndexClient {


//    public List<String> save(List<AddressIndexModel> documents) {
//        List<IndexQuery> indexQueries = elasticIndexUtil.getIndexQueries(documents);
//        List<String> documentIds = elasticsearchOperations.bulkIndex(
//                indexQueries,
//                IndexCoordinates.of(elasticConfigData.getIndexName())
//        ).stream().map(IndexedObjectInformation::id).collect(Collectors.toList());
//        LOG.info("Documents indexed successfully with type: {} and ids: {}", TwitterIndexModel.class.getName(),
//                documentIds);
//        return documentIds;
//    }
}
