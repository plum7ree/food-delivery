package com.example.addresssearch.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Builder
@Document(indexName = "#{@addressSearchElasticConfigData.indexName}")
public class AddressIndexModel {
    @JsonProperty
    private String id;

    @JsonProperty
    private String address;

    @JsonProperty
    private String postalCode;
}