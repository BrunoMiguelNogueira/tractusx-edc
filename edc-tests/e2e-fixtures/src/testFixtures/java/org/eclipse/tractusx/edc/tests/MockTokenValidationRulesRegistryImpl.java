package org.eclipse.tractusx.edc.tests;

import org.eclipse.edc.token.spi.TokenValidationRule;
import org.eclipse.edc.token.spi.TokenValidationRulesRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class MockTokenValidationRulesRegistryImpl implements TokenValidationRulesRegistry {
    private final Map<String, List<TokenValidationRule>> rules = new HashMap<>();

    @Override
    public void addRule(String context, TokenValidationRule rule) {
        rules.computeIfAbsent(context, s -> new ArrayList<>())
                .add(rule);
    }

    @Override
    public List<TokenValidationRule> getRules(String context) {
        return Collections.unmodifiableList(rules.getOrDefault(context, emptyList()));
    }
}
