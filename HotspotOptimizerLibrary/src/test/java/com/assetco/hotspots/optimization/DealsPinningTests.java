package com.assetco.hotspots.optimization;

import com.assetco.search.results.*;
import org.junit.jupiter.api.*;

public class DealsPinningTests extends OptimizerTests {
    @Test
    public void singleAsset() {
        // parameterized initial case
        singleAssetCase(partnerVendor, "0", "1000", true, true);
        // easy to generate many, many new cases in a very small amount of time
        singleAssetCase(partnerVendor, "0", "1000", false, true);
        singleAssetCase(partnerVendor, "200", "1000", true, true);
        singleAssetCase(partnerVendor, "600", "1000", true, true);
        // run, fix expectation, repeat
        singleAssetCase(partnerVendor, "999.99", "1000", true, false);
        singleAssetCase(partnerVendor, "700", "1000", true, true);
        singleAssetCase(partnerVendor, "700.001", "1000", true, false);
        singleAssetCase(partnerVendor, "7", "10", true, true);
        singleAssetCase(goldVendor, "0", "1000", true, true);
        singleAssetCase(goldVendor, "0", "1000", false, true);
        singleAssetCase(goldVendor, "200", "1000", true, true);
        singleAssetCase(goldVendor, "600", "1000", true, true);
        singleAssetCase(goldVendor, "999.99", "1000", true, false);
        singleAssetCase(goldVendor, "700", "1000", true, true);
        singleAssetCase(goldVendor, "700", "1000", false, false);
        singleAssetCase(goldVendor, "700.001", "1000", true, false);
        singleAssetCase(goldVendor, "7", "10", true, false);
        singleAssetCase(silverVendor, "0", "1000", true, false);
        singleAssetCase(silverVendor, "0", "1000", false, false);
        singleAssetCase(silverVendor, "200", "1000", true, false);
        singleAssetCase(silverVendor, "600", "1000", true, false);
        singleAssetCase(silverVendor, "999.99", "1000", true, false);
        singleAssetCase(silverVendor, "700", "1000", true, false);
        singleAssetCase(silverVendor, "700.001", "1000", true, false);
        singleAssetCase(silverVendor, "0", "1500", true, true);
        singleAssetCase(silverVendor, "0", "1500", false, true);
        singleAssetCase(silverVendor, "200", "1500", true, true);
        singleAssetCase(silverVendor, "600", "1500", true, true);
        singleAssetCase(silverVendor, "600", "1500", false, true);
        singleAssetCase(silverVendor, "999.99", "1500", true, true);
        singleAssetCase(silverVendor, "999.99", "1500", false, false);
        singleAssetCase(silverVendor, "700", "1500", true, true);
        singleAssetCase(silverVendor, "700.001", "1500", true, true);
        singleAssetCase(silverVendor, "750.001", "1500", true, true);
        singleAssetCase(silverVendor, "1500.001", "2000", true, false);
        singleAssetCase(silverVendor, "7", "10", true, false);
        singleAssetCase(basicVendor, "0", "1000", true, false);
        singleAssetCase(basicVendor, "200", "1000", true, false);
        singleAssetCase(basicVendor, "600", "1000", true, false);
        singleAssetCase(basicVendor, "999.99", "1000", true, false);
        singleAssetCase(basicVendor, "700", "1000", true, false);
        singleAssetCase(basicVendor, "700.001", "1000", true, false);
        singleAssetCase(basicVendor, "7", "10", true, false);
    }
    
    // noticed, looking at the code, that having multiple levels of vendor affects outcome
    @Test
    public void highPayoutLowerGradeAsset() {
        highPayoutLowerGradeAssetCases(partnerVendor, goldVendor, "0", "1000", true, true);
        highPayoutLowerGradeAssetCases(partnerVendor, goldVendor, "0", "1000", false, false);
        highPayoutLowerGradeAssetCases(partnerVendor, goldVendor, "700", "1000", true, false);
        highPayoutLowerGradeAssetCases(partnerVendor, goldVendor, "500", "1000", true, true);
        highPayoutLowerGradeAssetCases(partnerVendor, goldVendor, "500.01", "1000", true, false);
        highPayoutLowerGradeAssetCases(partnerVendor, silverVendor, "0", "100000", true, true);
        highPayoutLowerGradeAssetCases(partnerVendor, silverVendor, "5000", "100000", true, true);
        highPayoutLowerGradeAssetCases(partnerVendor, silverVendor, "5000", "100000", true, true);
        highPayoutLowerGradeAssetCases(goldVendor, silverVendor, "0", "100000", true, true);
        highPayoutLowerGradeAssetCases(goldVendor, silverVendor, "0", "100000", false, false);
        highPayoutLowerGradeAssetCases(goldVendor, silverVendor, "0", "2000", true, true);
        highPayoutLowerGradeAssetCases(goldVendor, silverVendor, "0", "1500", true, true);
        highPayoutLowerGradeAssetCases(goldVendor, silverVendor, "0", "1499.99", true, false);
        highPayoutLowerGradeAssetCases(goldVendor, silverVendor, "500", "1499.99", true, false);
        highPayoutLowerGradeAssetCases(goldVendor, silverVendor, "750", "1500", true, true);
        highPayoutLowerGradeAssetCases(goldVendor, silverVendor, "750.001", "1500", true, false);
    }

    @Test
    public void lowPayoutLowerGradeAssetCases() {

    }

    private void singleAssetCase(AssetVendor vendor, String royalties, String revenue, boolean isDealEligible, boolean shouldBeAdded) {
        setUp();

        var candidate = givenAssetWith30DayProfitabilityAndDealEligibility(vendor, royalties, revenue, isDealEligible);

        whenOptimize();

        thenAssetAdded(candidate, shouldBeAdded);
    }

    private void highPayoutLowerGradeAssetCases(AssetVendor highPayoutVendor, AssetVendor vendor, String royalties, String revenue, boolean isDealEligible, boolean shouldBeAdded) {
        setUp();

        var highPayout = givenAssetWith30DayProfitabilityAndDealEligibility(highPayoutVendor, "0", "1000000.00", true);
        var candidate = givenAssetWith30DayProfitabilityAndDealEligibility(vendor, royalties, revenue, isDealEligible);

        whenOptimize();

        thenHotspotHas(HotspotKey.Deals, highPayout);
        thenAssetAdded(candidate, shouldBeAdded);
    }

    private void thenAssetAdded(Asset candidate, boolean shouldBeAdded) {
        if (shouldBeAdded)
            thenHotspotHas(HotspotKey.Deals, candidate);
        else
            thenHotspotDoesNotHave(HotspotKey.Deals, candidate);
    }
}
