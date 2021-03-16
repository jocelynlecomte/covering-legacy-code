package com.assetco.hotspots.optimization;

import com.assetco.search.results.Asset;
import com.assetco.search.results.AssetVendor;
import com.assetco.search.results.HotspotKey;
import org.junit.jupiter.api.Test;

public class RelationshipPinningTests extends OptimizerTests {

    @Test
    public void singleAsset() {
        singleAssetCase(partnerVendor, "0", "1000", true, false, true, true, false);

        singleAssetCase(goldVendor, "0", "1000", true, false, true, true, false);

        singleAssetCase(silverVendor, "0", "1000", true, false, false, true, false);

        singleAssetCase(basicVendor, "0", "1000", true, false, false, false, false);
    }

    private void singleAssetCase(
            AssetVendor vendor,
            String royalties,
            String revenue,
            boolean isDealEligible,
            boolean shouldBeAddedToTopPicks,
            boolean shouldBeAddedToHighValue,
            boolean shouldBeAddedToFold,
            boolean shouldBeAddedToShowcase) {

        setUp();

        var candidate = givenAssetWith30DayProfitabilityAndDealEligibility(vendor, royalties, revenue, isDealEligible);

        whenOptimize();

        thenAssetAdded(candidate, shouldBeAddedToTopPicks, shouldBeAddedToHighValue, shouldBeAddedToFold, shouldBeAddedToShowcase);
    }

    private void thenAssetAdded(
            Asset candidate,
            boolean shouldBeAddedToTopPicks,
            boolean shouldBeAddedToHighValue,
            boolean shouldBeAddedToFold,
            boolean shouldBeAddedToShowcase) {
        if (shouldBeAddedToTopPicks)
            thenHotspotHas(HotspotKey.TopPicks, candidate);
        else
            thenHotspotDoesNotHave(HotspotKey.TopPicks, candidate);
        if (shouldBeAddedToHighValue)
            thenHotspotHas(HotspotKey.HighValue, candidate);
        else
            thenHotspotDoesNotHave(HotspotKey.HighValue, candidate);
        if (shouldBeAddedToFold)
            thenHotspotHas(HotspotKey.Fold, candidate);
        else
            thenHotspotDoesNotHave(HotspotKey.Fold, candidate);
        if (shouldBeAddedToShowcase)
            thenHotspotHas(HotspotKey.Showcase, candidate);
        else
            thenHotspotDoesNotHave(HotspotKey.Showcase, candidate);
    }
}
