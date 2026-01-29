package org.eclipse.tractusx.edc.tests;

import com.nimbusds.jwt.SignedJWT;
import org.eclipse.edc.iam.decentralizedclaims.spi.validation.TokenValidationAction;
import org.eclipse.edc.jwt.validation.jti.JtiValidationStore;
import org.eclipse.edc.keys.spi.PublicKeyResolver;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.token.rules.ExpirationIssuedAtValidationRule;
import org.eclipse.edc.token.rules.NotBeforeValidationRule;
import org.eclipse.edc.token.spi.TokenValidationRule;
import org.eclipse.edc.token.spi.TokenValidationService;
import org.eclipse.edc.verifiablecredentials.jwt.rules.HasSubjectRule;
import org.eclipse.edc.verifiablecredentials.jwt.rules.IssuerEqualsSubjectRule;
import org.eclipse.edc.verifiablecredentials.jwt.rules.IssuerKeyIdValidationRule;
import org.eclipse.edc.verifiablecredentials.jwt.rules.JtiValidationRule;
import org.eclipse.edc.verifiablecredentials.jwt.rules.SubJwkIsNullRule;
import org.eclipse.edc.verifiablecredentials.jwt.rules.TokenNotNullRule;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

public class MockTokenValidationAction implements TokenValidationAction {

    private final TokenValidationService tokenValidationService;
    private final PublicKeyResolver publicKeyResolver;
    private final JtiValidationStore jtiValidationStore;
    private final ArrayList<TokenValidationRule> rules = new ArrayList<TokenValidationRule>();
    private final Clock clock = Clock.systemUTC();

    public MockTokenValidationAction(TokenValidationService tokenValidationService, PublicKeyResolver publicKeyResolver, JtiValidationStore jtiValidationStore) {
        this.tokenValidationService = tokenValidationService;
        this.publicKeyResolver = publicKeyResolver;
        this.jtiValidationStore = jtiValidationStore;
    }

    public Result<ClaimToken> validate(String participantContextId, TokenRepresentation tokenRepresentation) {
        try {
            var signedJwt = SignedJWT.parse(tokenRepresentation.getToken());
            System.out.println("A1");
            var keyId = signedJwt.getHeader().getKeyID();

            rules.add(new HasSubjectRule());
            rules.add(new IssuerEqualsSubjectRule());
            rules.add(new IssuerKeyIdValidationRule(keyId));
            rules.add(new SubJwkIsNullRule());
            rules.add(new JtiValidationRule(jtiValidationStore, null));
            rules.add(new TokenNotNullRule());

            rules.add(new ExpirationIssuedAtValidationRule(clock, 5, false));
            rules.add(new NotBeforeValidationRule(clock, 4, true));
            System.out.println("A2");
            return tokenValidationService.validate(tokenRepresentation, publicKeyResolver, rules);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
