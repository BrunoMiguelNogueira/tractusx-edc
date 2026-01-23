package org.eclipse.tractusx.edc.tests;

import org.eclipse.edc.iam.decentralizedclaims.spi.validation.TokenValidationAction;
import org.eclipse.edc.jwt.validation.jti.JtiValidationEntry;
import org.eclipse.edc.jwt.validation.jti.JtiValidationStore;
import org.eclipse.edc.keys.spi.PublicKeyResolver;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.token.InMemoryJtiValidationStore;
import org.eclipse.edc.token.spi.TokenValidationRule;
import org.eclipse.edc.token.spi.TokenValidationService;

import java.util.List;

public class MockTokenValidationAction implements TokenValidationAction {

    private final TokenValidationService tokenValidationService;
    private final PublicKeyResolver publicKeyResolver;
    private final JtiValidationStore jtiValidationStore;

    public MockTokenValidationAction(TokenValidationService tokenValidationService, PublicKeyResolver publicKeyResolver, JtiValidationStore jtiValidationStore) {
        this.tokenValidationService = tokenValidationService;
        this.publicKeyResolver = publicKeyResolver;
        this.jtiValidationStore = jtiValidationStore;
    }

    public Result<ClaimToken> validate(String participantContextId, TokenRepresentation tokenRepresentation) {
//        try {
//            var signedJwt = SignedJWT.parse(tokenRepresentation.getToken());
//            var keyId = signedJwt.getHeader().getKeyID();
//            var rules = new ArrayList<>(rulesRegistry.getRules(DCP_SELF_ISSUED_TOKEN_CONTEXT));
//            rules.add(new IssuerKeyIdValidationRule(keyId));
//            rules.add(new AudienceValidationRule(didResolver.apply(participantContextId)));
        List<TokenValidationRule> rules = List.of(new MockJtiValidationRule(jtiValidationStore));
        return tokenValidationService.validate(tokenRepresentation, publicKeyResolver, rules);
//        } catch (ParseException e) {
//            throw new EdcException(e);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//        return tokenValidationService.validate(tokenRepresentation, Mockito.mock(PublicKeyResolver.class), List.of());

    }
}
