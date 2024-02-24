package com.example.addresssearch.repository;

import com.example.addresssearch.data.AddressIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AddressRepository extends ElasticsearchRepository<AddressIndexModel, String> {
}
