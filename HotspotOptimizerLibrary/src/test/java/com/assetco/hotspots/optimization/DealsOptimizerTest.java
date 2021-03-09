package com.assetco.hotspots.optimization;

import com.assetco.search.results.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DealsOptimizerTest {
    private DealsOptimizer optimizer = new DealsOptimizer();

    @Test
    void optimizeUneligibleAssetFromSilverVendorShouldFail() {
        AssetVendor vendor = createAssetVendor("vendor", AssetVendorRelationshipLevel.Silver, 0.5F);
        AssetPurchaseInfo purchaseInfo = createAssetPurchaseInfo(1,0,0,0);
        Asset asset = createAsset("asset", purchaseInfo, vendor);

        SearchResults results = new SearchResults();
        results.addFound(asset);

        AssetAssessments assetAssessments = asset1 -> false;

        optimizer.optimize(results, assetAssessments);

        assertTrue(results.getHotspot(HotspotKey.Deals).getMembers().isEmpty());
    }

    @Test
    void optimizeAssetFromPartnerVendorShouldBeOk() {
        AssetVendor vendor = createAssetVendor("vendor", AssetVendorRelationshipLevel.Partner, 0.5F);
        AssetPurchaseInfo purchaseInfo = createAssetPurchaseInfo(1,1,1000,0);
        Asset asset = createAsset("asset", purchaseInfo, vendor);

        SearchResults results = new SearchResults();
        results.addFound(asset);

        AssetAssessments assetAssessments = asset1 -> false;

        optimizer.optimize(results, assetAssessments);

        assertEquals(1, results.getHotspot(HotspotKey.Deals).getMembers().size());
        assertEquals(asset, results.getHotspot(HotspotKey.Deals).getMembers().get(0));
    }

    private Asset createAsset(String title, AssetPurchaseInfo purchaseInfo, AssetVendor vendor) {
        return new Asset(
                title,
                title,
                URI.create(""),
                URI.create(""),
                purchaseInfo,
                purchaseInfo,
                Collections.emptyList(),
                vendor
                );
    }

    private AssetPurchaseInfo createAssetPurchaseInfo(
            long timesShown,
            long timesPurchased,
            long totalRevenue,
            long totalRoyaltiesOwed
            ) {

        return new AssetPurchaseInfo(
                timesShown,
                timesPurchased,
                createMoney(totalRevenue),
                createMoney(totalRoyaltiesOwed));
    }

    private Money createMoney(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    private List<AssetTopic> createAssetTopics(String... topics) {
        return Arrays.stream(topics).map(topic -> new AssetTopic(topic, topic)).collect(Collectors.toList());
    }

    private AssetVendor createAssetVendor(String name, AssetVendorRelationshipLevel relationshipLevel, float royaltyRate) {
        return new AssetVendor(name, name, relationshipLevel, royaltyRate);
    }
}
