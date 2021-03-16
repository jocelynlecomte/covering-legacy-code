package com.assetco.hotspots.optimization;

import com.assetco.hotspots.optimization.*;
import com.assetco.search.results.*;
import org.junit.jupiter.api.*;

import java.math.*;
import java.util.*;

import static com.assetco.search.results.AssetVendorRelationshipLevel.*;
import static org.junit.jupiter.api.Assertions.*;

public class OptimizerTests {
    protected final int maximumShowcaseItems = 5;
    protected SearchResults searchResults;
    protected AssetVendor partnerVendor;
    protected AssetVendor goldVendor;
    protected AssetVendor silverVendor;
    protected AssetVendor basicVendor;
    protected SearchResultHotspotOptimizer optimizer;
    int topicId = 0;

    protected Map<Asset, Boolean> dealEligibility;

    @BeforeEach
    public void setUp() {
        dealEligibility = new HashMap<>();
        optimizer = new SearchResultHotspotOptimizer();
        optimizer.setAssessments(key -> dealEligibility.getOrDefault(key, false));
        searchResults = new SearchResults();
        partnerVendor = makeVendor(Partner);
        goldVendor = makeVendor(Gold);
        silverVendor = makeVendor(Silver);
        basicVendor = makeVendor(Basic);
    }

    protected AssetVendor makeVendor(AssetVendorRelationshipLevel relationshipLevel) {
        return new AssetVendor("anything", "anything", relationshipLevel, 1);
    }

    protected AssetPurchaseInfo getPurchaseInfoFromRoyaltiesAndRevenue(String royalties, String revenue) {
        return new AssetPurchaseInfo(0, 0,
                new Money(new BigDecimal(revenue)),
                new Money(new BigDecimal(royalties)));
    }

    protected Asset givenAssetInResultsWithVendor(AssetVendor vendor) {
        Asset result = getAsset(vendor);
        searchResults.addFound(result);
        return result;
    }

    protected Asset getAsset(AssetVendor vendor) {
        return new Asset("anything", "anything", null, null, getPurchaseInfo(), getPurchaseInfo(), new ArrayList<>(), vendor);
    }

    protected AssetPurchaseInfo getPurchaseInfo() {
        return getPurchaseInfoFromRoyaltiesAndRevenue("0", "0");
    }

    protected Asset givenDealEligibleAssetWith30DayProfitability(String royalties, String revenue) {
        var vendor = this.basicVendor;
        return givenAssetWith30DayProfitabilityAndDealEligibility(vendor, royalties, revenue, true);
    }

    protected Asset givenAssetWith30DayProfitabilityAndDealEligibility(AssetVendor vendor, String royalties, String revenue, boolean isDealEligible) {
        var result = new Asset("anything", "anything", null, null, getPurchaseInfoFromRoyaltiesAndRevenue(royalties, revenue),
                getPurchaseInfo(),
                new ArrayList<>(),
                vendor);

        searchResults.addFound(result);
        dealEligibility.put(result, isDealEligible);

        return result;
    }

    protected void thenHotspotOrderIs(HotspotKey key, Asset... expectedOrder) {
        var expectations = new LinkedList<>(Arrays.asList(expectedOrder));
        var members = new LinkedList<>(searchResults.getHotspot(key).getMembers());

        while (members.size() > 0) {
            var actual = members.remove();
            if (expectations.contains(actual))
                Assertions.assertSame(expectations.remove(), actual);
        }
    }

    protected void thenHotspotHasExactly(HotspotKey hotspotKey, List<Asset> expected) {
        Assertions.assertArrayEquals(expected.toArray(), searchResults.getHotspot(hotspotKey).getMembers().toArray());
    }

    protected void thenHotspotHasExactly(HotspotKey hotspotKey, Asset... expected) {
        thenHotspotHasExactly(hotspotKey, Arrays.asList(expected));
    }

    protected ArrayList<Asset> givenAssetsInResultsWithVendor(int count, AssetVendor vendor) {
        var result = new ArrayList<Asset>();
        for (var i = 0; i < count; ++i) {
            result.add(givenAssetInResultsWithVendor(vendor));
        }
        return result;
    }

    protected void whenOptimize() {
        optimizer.optimize(searchResults);
    }

    protected void givenSearchResultHotspotHas(HotspotKey key, Asset... assets) {
        for (var asset : assets)
            searchResults.getHotspot(key).addMember(asset);
    }

    protected Asset givenAssetWith24HourTraffic(int shown, int sold) {
        var result = new Asset(null, null, null, null, getPurchaseInfo(), getPurchaseInfoForTraffic(shown, sold), new ArrayList<>(), basicVendor);
        searchResults.addFound(result);

        return result;
    }

    protected Asset givenAssetWith30DayTraffic(int shown, int sold) {
        var result = new Asset(null, null, null, null, getPurchaseInfoForTraffic(shown, sold), getPurchaseInfo(), new ArrayList<>(), basicVendor);
        searchResults.addFound(result);

        return result;
    }

    private AssetPurchaseInfo getPurchaseInfoForTraffic(int shown, int sold) {
        return new AssetPurchaseInfo(shown, sold, new Money(new BigDecimal("0")), new Money(new BigDecimal("0")));
    }

    protected void thenHotspotDoesNotHave(HotspotKey key, Asset... forbidden) {
        thenHotspotDoesNotHave(key, Arrays.asList(forbidden));
    }

    protected List<Asset> givenAssetsWithTopics(AssetVendor vendor, int count, AssetTopic... topics) {
        var result = new ArrayList<Asset>();
        for (var i = 0; i < count; ++i)
            result.add(givenAssetWithTopics(vendor, topics));

        return result;
    }

    protected Asset givenAssetWithTopics(AssetVendor vendor, AssetTopic... topics) {
        var actualTopics = new ArrayList<AssetTopic>();
        for (var topic : topics)
            actualTopics.add(topic != null ? new AssetTopic(topic.getId(), topic.getDisplayName()) : null);

        var result = new Asset(null, null, null, null, getPurchaseInfo(), getPurchaseInfo(), actualTopics, vendor);
        searchResults.addFound(result);
        return result;
    }

    protected AssetTopic makeTopic() {
        return new AssetTopic("id-" + (++topicId), "anything");
    }

    protected void setHotTopics(AssetTopic... topics) {
        optimizer.setHotTopics(() -> Arrays.asList(topics));
    }

    protected void thenHotspotDoesNotHave(HotspotKey key, List<Asset> forbidden) {
        for (var asset : forbidden)
            assertFalse(searchResults.getHotspot(key).getMembers().contains(asset));
    }
    protected void thenHotspotHas(HotspotKey hotspotKey, Asset... expectedAssets) {
        thenHotspotHas(hotspotKey, Arrays.asList(expectedAssets));
    }

    protected void thenHotspotHas(HotspotKey hotspotKey, List<Asset> expectedAssets) {
        for (var expectedAsset : expectedAssets) {
            Assertions.assertTrue(searchResults.getHotspot(hotspotKey).getMembers().contains(expectedAsset));
        }
    }

    protected AssetPurchaseInfo getPurchaseInfoFromTraffic(int shown, int sold) {
        return new AssetPurchaseInfo(shown, sold, new Money(new BigDecimal("0")), new Money(new BigDecimal("0")));
    }

    protected void thenHotSpotCountIs(HotspotKey key, int count) {
        assertEquals(count, searchResults.getHotspot(key).getMembers().size(), key.toString());
    }
}

