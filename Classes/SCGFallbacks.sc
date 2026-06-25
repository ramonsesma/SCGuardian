SCGFallbacks {
    *resolve { |spec, policy, report|
        var policyData;
        var fallbackChains;
        var classes;
        var role;
        var chain;
        var chosen;
        var preferred;

        policyData = policy ? IdentityDictionary.new;
        fallbackChains = policyData[\fallbackChains] ? IdentityDictionary.new;
        classes = (report ? IdentityDictionary.new)[\classes] ? IdentityDictionary.new;
        role = (spec ? IdentityDictionary.new)[\role].asSymbol;
        chain = fallbackChains[role] ? Array.new;
        preferred = chain.first;

        chosen = chain.detect { |candidate|
            ((classes[candidate] ? (available: false))[\available]) == true
        };

        if(chosen.isNil) {
            if((policyData[\allowFallback] ? true) == false) {
                Error("missing_dependency: no allowed fallback for %".format(role)).throw;
            };

            Error("missing_dependency: no available implementation for %".format(role)).throw;
        };

        ^(
            request: spec,
            chosen: chosen,
            degraded: chosen != preferred,
            explanation: if(chosen == preferred) {
                "preferred implementation available"
            } {
                "preferred implementation unavailable; selected fallback %".format(chosen)
            },
            warnings: if(chosen == preferred) {
                Array.new
            } {
                ["fallback applied"]
            }
        )
    }
}
