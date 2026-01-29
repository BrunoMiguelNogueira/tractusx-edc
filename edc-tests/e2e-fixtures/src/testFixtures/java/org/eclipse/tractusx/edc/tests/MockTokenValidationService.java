package org.eclipse.tractusx.edc.tests;

import com.nimbusds.jwt.SignedJWT;
import org.eclipse.edc.keys.spi.PublicKeyResolver;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.AbstractResult;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.token.spi.TokenValidationRule;
import org.eclipse.edc.token.spi.TokenValidationService;

import java.text.ParseException;
import java.util.List;

public class MockTokenValidationService implements TokenValidationService {

    private TokenValidationRule tokenValidationRule;

    @Override
    public Result<ClaimToken> validate(TokenRepresentation tokenRepresentation, PublicKeyResolver publicKeyResolver, List<TokenValidationRule> rules) {
        var token = tokenRepresentation.getToken();
        var additional = tokenRepresentation.getAdditional();
        try {
            var signedJwt = SignedJWT.parse(token);

            var tokenBuilder = ClaimToken.Builder.newInstance();
            signedJwt.getJWTClaimsSet().getClaims().entrySet().stream()
                    .filter(entry -> entry.getValue() != null)
                    .forEach(entry -> tokenBuilder.claim(entry.getKey(), entry.getValue()));

            var claimToken = tokenBuilder.build();

            System.out.println("S1");

            var errors = rules.stream()
                    .map(r -> r.checkRule(claimToken, additional))
                    .reduce(Result::merge)
                    .stream()
                    .filter(AbstractResult::failed)
                    .flatMap(r -> r.getFailureMessages().stream())
                    .toList();

            if (!errors.isEmpty()) {
                return Result.failure(errors);
            }

            System.out.println("S2");

            return Result.success(claimToken);
        } catch (ParseException e) {
            return Result.failure("Failed to decode token");
        }
    }
}
