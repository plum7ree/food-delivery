package com.example.eatsorderdomain.data.aggregate;

import com.example.commondata.domain.aggregate.AggregateRoot;
import com.example.commondata.domain.aggregate.valueobject.SimpleId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Restaurant extends AggregateRoot<SimpleId> {
    private List<Product> products;
    private boolean active;

}
