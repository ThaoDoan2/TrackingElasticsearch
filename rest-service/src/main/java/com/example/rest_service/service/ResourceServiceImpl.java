package com.example.rest_service.service;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.rest_service.dto.ResourceDTO;
import com.example.rest_service.repository.resource.ResourceDocument;
import com.example.rest_service.repository.resource.ResourceRepository;
import com.example.rest_service.search.ElasticsearchProxy;
import com.example.rest_service.search.SearchFilters;
import com.example.rest_service.search.query.QueryType;
import com.example.rest_service.search.query.SearchMeta;
import com.example.rest_service.service.converter.ResourceDTOConverter;

@Service
public class ResourceServiceImpl implements IResourceService {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceServiceImpl.class);

    private final ResourceRepository repository;
    private final ResourceDTOConverter converter;
    private final ElasticsearchProxy<ResourceDocument, ResourceDTO> client;

    public ResourceServiceImpl(ResourceRepository repository,
            ResourceDTOConverter converter,
            ElasticsearchProxy<ResourceDocument, ResourceDTO> client) {
        this.repository = repository;
        this.converter = converter;
        this.client = client;
    }

    @Override
    public void save(ResourceDocument resource) {
        repository.save(resource);
    }

    @Override
    public void save(ResourceDTO resource) {
        repository.save(converter.convertToDocument(resource));
    }

    @Override
    public List<ResourceDTO> search(SearchFilters filters) {
        return client.search(
                filters,
                new SearchMeta(List.of("userId", "gameId", "eventType", "itemName"), "resource", QueryType.MATCH),
                ResourceDocument.class);
    }

    @Override
    public void initData() {
        String countries[] = {"US", "UK", "DE", "FR", "JP"};
        String versions[] = {"1.0.0", "1.1.0", "1.2.0"};

        int itemIds[] = {0,1,2,3,4,5,6,7,8};
        String itemNams[] = {"Gold", "Hammer", "Bonus", "Clock", "Hint", "TimeStop" , "Shuffle", "Transform", "Life"};
        String placements[] = {"BattlePass", "Rewarded", "DailyQuest", "Prepare", "CardCollection", "Shop"};
        String subPlacements[] = {"Training", "Starter", "BasicBundle", "BigBundle"};
        for (int i = 0; i < 100; i ++ ){
            int r = new Random().nextInt(1000);
            int r2 = new Random().nextInt(1000);
            ResourceDocument doc = new ResourceDocument();
            doc.setUserId("user" + (r % 50));
            doc.setGameId("com.higame.goods.sorting.match.triple.master");
            doc.setEventType(r %3 == 0 ? "Sink": "Source");
            doc.setPlatform(r % 2 == 0 ? "Android" : "iOS");
            doc.setCountry(countries[r % countries.length]);
            doc.setGameVersion(versions[r % versions.length]);
            doc.setLoggedDay((long)r %7);
            Date now = new Date();
            Date accountCreatedDate = new Date(now.getTime() - r * 86400 * 1000L);
            doc.setAccountCreatedDate(accountCreatedDate);
            Date recordDate = new Date(now.getTime() - (r % 7) * 86400 * 1000L);
            doc.setDate(recordDate);
            
            doc.setItemId(itemIds[r % itemIds.length]);
            doc.setItemName(itemNams[r % itemNams.length]);
            doc.setAmount((long)r * 10);
            
            if (doc.getEventType().equals("Source")){
                doc.setPlacement(placements[r % placements.length]);
                if (doc.getPlacement().equals("Shop"))
                    doc.setSubPlacement(subPlacements[r2 % subPlacements.length]);
            } else {
                doc.setPlacement(r2 %2 == 0?"Prepare":"InGame");
            }
            repository.save(doc);
        }
        LOG.info("Sample data initialized");
    }
}
