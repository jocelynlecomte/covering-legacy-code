package com.assetco.hotspots.optimization;

import com.assetco.hotspots.optimization.*;
import com.assetco.search.results.*;
import org.junit.jupiter.api.*;

import java.math.*;
import java.util.*;

import static com.assetco.search.results.AssetVendorRelationshipLevel.*;
import static com.assetco.search.results.HotspotKey.*;

public class BugsTests {
    private final int maximumShowcaseItems = 5;
    private SearchResults searchResults;
    private AssetVendor partnerVendor;
    private AssetVendor basicVendor;
    private SearchResultHotspotOptimizer optimizer;
    int topicId = 0;

    @Test
    public void prevailingPartnerReceivesFirstFiveItemsInShowcase() {
        ArrayList<Asset> expected = new ArrayList<>();
        expected.add(givenAssetInResultsWithVendor(partnerVendor));
        givenAssetInResultsWithVendor(makeVendor(Partner));
        expected.addAll(givenAssetsInResultsWithVendor(maximumShowcaseItems - 1, partnerVendor));

        whenOptimize();

        thenHotspotHasExactly(Showcase, expected);
    }

    @Test
    public void allItemsDeservingHighlightAreHighlighted() {
        AssetTopic moreHotTopic = makeTopic();
        AssetTopic lessHotTopic = makeTopic();
        setHotTopics(moreHotTopic, lessHotTopic);
        var expectedAssets = givenAssetsWithTopics(basicVendor, 2, lessHotTopic);
        givenAssetsWithTopics(basicVendor, 3, moreHotTopic);
        expectedAssets.add(givenAssetWithTopics(basicVendor, lessHotTopic));

        whenOptimize();

        thenHotspotHas(Highlight, expectedAssets);
    }

    @Test
    public void itemsWithHighRecentAndLastMonthVolumeSingleEntered() {
        var asset = new Asset(
                null, null, null, null,
                getPurchaseInfoFromTraffic(50000, 50000),
                getPurchaseInfoFromTraffic(4000, 4000),
                new ArrayList<>(),
                basicVendor);
        searchResults.addFound(asset);

        whenOptimize();

        // initially, this was...
        // thenHotspotHasExactly(HighValue, asset, asset);
        thenHotspotHasExactly(HighValue, asset);
    }

    @BeforeEach
    public void setUp() {
        optimizer = new SearchResultHotspotOptimizer();
        searchResults = new SearchResults();
        partnerVendor = makeVendor(Partner);
        basicVendor = makeVendor(Basic);
    }

    private AssetVendor makeVendor(AssetVendorRelationshipLevel relationshipLevel) {
        return new AssetVendor("anything", "anything", relationshipLevel, 1);
    }

    private AssetPurchaseInfo getPurchaseInfoFromRoyaltiesAndRevenue(String royalties, String revenue) {
        return new AssetPurchaseInfo(0, 0,
                new Money(new BigDecimal(revenue)),
                new Money(new BigDecimal(royalties)));
    }

    private Asset givenAssetInResultsWithVendor(AssetVendor vendor) {
        Asset result = getAsset(vendor);
        searchResults.addFound(result);
        return result;
    }

    private Asset getAsset(AssetVendor vendor) {
        return new Asset("anything", "anything", null, null, getPurchaseInfo(), getPurchaseInfo(), new ArrayList<>(), vendor);
    }

    private AssetPurchaseInfo getPurchaseInfo() {
        return getPurchaseInfoFromRoyaltiesAndRevenue("0", "0");
    }

    private void thenHotspotHasExactly(HotspotKey hotspotKey, List<Asset> expected) {
        Assertions.assertArrayEquals(expected.toArray(), searchResults.getHotspot(hotspotKey).getMembers().toArray());
    }

    private void thenHotspotHasExactly(HotspotKey hotspotKey, Asset... expected) {
        thenHotspotHasExactly(hotspotKey, Arrays.asList(expected));
    }

    private ArrayList<Asset> givenAssetsInResultsWithVendor(int count, AssetVendor vendor) {
        var result = new ArrayList<Asset>();
        for (var i = 0; i < count; ++i) {
            result.add(givenAssetInResultsWithVendor(vendor));
        }
        return result;
    }

    private void whenOptimize() {
        optimizer.optimize(searchResults);
    }

    private List<Asset> givenAssetsWithTopics(AssetVendor vendor, int count, AssetTopic... topics) {
        var result = new ArrayList<Asset>();
        for (var i = 0; i < count; ++i)
            result.add(givenAssetWithTopics(vendor, topics));

        return result;
    }

    private Asset givenAssetWithTopics(AssetVendor vendor, AssetTopic... topics) {
        var actualTopics = new ArrayList<AssetTopic>();
        for (var topic : topics)
            actualTopics.add(new AssetTopic(topic.getId(), topic.getDisplayName()));

        var result = new Asset(null, null, null, null, getPurchaseInfo(), getPurchaseInfo(), actualTopics, vendor);
        searchResults.addFound(result);
        return result;
    }

    private AssetTopic makeTopic() {
        return new AssetTopic("id-" + (++topicId), "anything");
    }

    private void setHotTopics(AssetTopic... topics) {
        optimizer.setHotTopics(() -> Arrays.asList(topics));
    }

    private void thenHotspotHas(HotspotKey hotspotKey, List<Asset> expectedAssets) {
        for (var expectedAsset : expectedAssets) {
            Assertions.assertTrue(searchResults.getHotspot(hotspotKey).getMembers().contains(expectedAsset));
        }
    }

    private AssetPurchaseInfo getPurchaseInfoFromTraffic(int shown, int sold) {
        return new AssetPurchaseInfo(shown, sold, new Money(new BigDecimal("0")), new Money(new BigDecimal("0")));
    }
}
