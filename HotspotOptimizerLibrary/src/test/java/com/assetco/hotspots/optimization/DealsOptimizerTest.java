package com.assetco.hotspots.optimization;

import com.assetco.search.results.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.assetco.search.results.AssetVendorRelationshipLevel.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DealsOptimizerTest {
    private DealsOptimizer optimizer = new DealsOptimizer();
    private SearchResults searchResults;
    private AssetAssessments rejectAllAssessor = anyAsset -> false;
    private AssetAssessments acceptAllAssessor = anyAsset -> true;
    protected AssetVendor partnerVendor;
    protected AssetVendor goldVendor;
    protected AssetVendor silverVendor;
    protected AssetVendor basicVendor;

    @BeforeEach
    private void init() {
        searchResults = new SearchResults();
        partnerVendor = createVendor(Partner);
        goldVendor = createVendor(Gold);
        silverVendor = createVendor(Silver);
        basicVendor = createVendor(Basic);
    }

    @Test
    void singleAssetPinningTests() {
        // Partner tests
        singleAssetTestCase(partnerVendor, 1000, 0, rejectAllAssessor, true);
        singleAssetTestCase(partnerVendor, 1000, 200, rejectAllAssessor, true);
        singleAssetTestCase(partnerVendor, 1000, 400, rejectAllAssessor, true);
        singleAssetTestCase(partnerVendor, 1000, 600, rejectAllAssessor, true);
        singleAssetTestCase(partnerVendor, 1000, 800, rejectAllAssessor, false);
        singleAssetTestCase(partnerVendor, 1000, 1000, rejectAllAssessor, false);
        singleAssetTestCase(partnerVendor, 1500, 0, rejectAllAssessor, true);

        singleAssetTestCase(partnerVendor, 1000, 0, acceptAllAssessor, true);
        singleAssetTestCase(partnerVendor, 1000, 200, acceptAllAssessor, true);
        singleAssetTestCase(partnerVendor, 1000, 400, acceptAllAssessor, true);
        singleAssetTestCase(partnerVendor, 1000, 600, acceptAllAssessor, true);
        singleAssetTestCase(partnerVendor, 1000, 800, acceptAllAssessor, false);
        singleAssetTestCase(partnerVendor, 1000, 1000, acceptAllAssessor, false);
        singleAssetTestCase(partnerVendor, 1500, 0, acceptAllAssessor, true);

        // Gold vendor tests
        singleAssetTestCase(goldVendor, 1000, 0, rejectAllAssessor, true);
        singleAssetTestCase(goldVendor, 1000, 200, rejectAllAssessor, true);
        singleAssetTestCase(goldVendor, 1000, 400, rejectAllAssessor, true);
        singleAssetTestCase(goldVendor, 1000, 600, rejectAllAssessor, false);
        singleAssetTestCase(goldVendor, 1000, 800, rejectAllAssessor, false);
        singleAssetTestCase(goldVendor, 1000, 1000, rejectAllAssessor, false);
        singleAssetTestCase(goldVendor, 1500, 0, rejectAllAssessor, true);

        singleAssetTestCase(goldVendor, 1000, 0, acceptAllAssessor, true);
        singleAssetTestCase(goldVendor, 1000, 200, acceptAllAssessor, true);
        singleAssetTestCase(goldVendor, 1000, 400, acceptAllAssessor, true);
        singleAssetTestCase(goldVendor, 1000, 600, acceptAllAssessor, true);
        singleAssetTestCase(goldVendor, 1000, 800, acceptAllAssessor, false);
        singleAssetTestCase(goldVendor, 1000, 1000, acceptAllAssessor, false);
        singleAssetTestCase(goldVendor, 1500, 0, acceptAllAssessor, true);

        // Silver vendor tests
        singleAssetTestCase(silverVendor, 1000, 0, rejectAllAssessor, false);
        singleAssetTestCase(silverVendor, 1000, 200, rejectAllAssessor, false);
        singleAssetTestCase(silverVendor, 1000, 400, rejectAllAssessor, false);
        singleAssetTestCase(silverVendor, 1000, 600, rejectAllAssessor, false);
        singleAssetTestCase(silverVendor, 1000, 800, rejectAllAssessor, false);
        singleAssetTestCase(silverVendor, 1000, 1000, rejectAllAssessor, false);
        singleAssetTestCase(silverVendor, 1500, 0, rejectAllAssessor, true);

        singleAssetTestCase(silverVendor, 1000, 0, acceptAllAssessor, false);
        singleAssetTestCase(silverVendor, 1000, 200, acceptAllAssessor, false);
        singleAssetTestCase(silverVendor, 1000, 400, acceptAllAssessor, false);
        singleAssetTestCase(silverVendor, 1000, 600, acceptAllAssessor, false);
        singleAssetTestCase(silverVendor, 1000, 800, acceptAllAssessor, false);
        singleAssetTestCase(silverVendor, 1000, 1000, acceptAllAssessor, false);
        singleAssetTestCase(silverVendor, 1500, 0, acceptAllAssessor, true);
        
        // Basic vendor tests
        singleAssetTestCase(basicVendor, 1000, 0, rejectAllAssessor, false);
        singleAssetTestCase(basicVendor, 1000, 200, rejectAllAssessor, false);
        singleAssetTestCase(basicVendor, 1000, 400, rejectAllAssessor, false);
        singleAssetTestCase(basicVendor, 1000, 600, rejectAllAssessor, false);
        singleAssetTestCase(basicVendor, 1000, 800, rejectAllAssessor, false);
        singleAssetTestCase(basicVendor, 1000, 1000, rejectAllAssessor, false);
        singleAssetTestCase(basicVendor, 1500, 0, rejectAllAssessor, false);

        singleAssetTestCase(basicVendor, 1000, 0, acceptAllAssessor, false);
        singleAssetTestCase(basicVendor, 1000, 200, acceptAllAssessor, false);
        singleAssetTestCase(basicVendor, 1000, 400, acceptAllAssessor, false);
        singleAssetTestCase(basicVendor, 1000, 600, acceptAllAssessor, false);
        singleAssetTestCase(basicVendor, 1000, 800, acceptAllAssessor, false);
        singleAssetTestCase(basicVendor, 1000, 1000, acceptAllAssessor, false);
        singleAssetTestCase(basicVendor, 1500, 0, acceptAllAssessor, false);
    }

    private void singleAssetTestCase(AssetVendor vendor, long revenue, long royalties, AssetAssessments assessor, boolean shouldBeAdded) {
        init();

        // GIVEN
        var purchaseInfo = createPurchaseInfo(revenue, royalties);
        var asset = createAssetAndAddToSearchResults(purchaseInfo, vendor);

        // WHEN
        optimizer.optimize(searchResults, assessor);

        // THEN
        if (shouldBeAdded) {
            assertThat(searchResults.getHotspot(HotspotKey.Deals).getMembers()).containsOnly(asset);
        }
        else {
            assertThat(searchResults.getHotspot(HotspotKey.Deals).getMembers()).doesNotContain(asset);
        }
    }

    private Asset createAssetAndAddToSearchResults(AssetPurchaseInfo purchaseInfo, AssetVendor vendor) {
        var asset = new Asset(
                "asset",
                "asset",
                null,
                null,
                purchaseInfo,
                purchaseInfo,
                Collections.emptyList(),
                vendor
                );

        searchResults.addFound(asset);
        return asset;
    }

    private AssetPurchaseInfo createPurchaseInfo(
            long totalRevenue,
            long totalRoyaltiesOwed
    ) {

        return new AssetPurchaseInfo(
                1,
                1,
                createMoney(totalRevenue),
                createMoney(totalRoyaltiesOwed));
    }

    private Money createMoney(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    private List<AssetTopic> createAssetTopics(String... topics) {
        return Arrays.stream(topics).map(topic -> new AssetTopic(topic, topic)).collect(Collectors.toList());
    }

    private AssetVendor createVendor(AssetVendorRelationshipLevel relationshipLevel) {
        return new AssetVendor("vendor", "vendor", relationshipLevel, (float) 0.5);
    }
}
