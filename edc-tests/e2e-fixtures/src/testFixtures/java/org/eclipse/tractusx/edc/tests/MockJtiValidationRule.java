package org.eclipse.tractusx.edc.tests;

import org.eclipse.edc.jwt.spi.JwtRegisteredClaimNames;
import org.eclipse.edc.jwt.validation.jti.JtiValidationEntry;
import org.eclipse.edc.jwt.validation.jti.JtiValidationStore;
import org.eclipse.edc.spi.iam.ClaimToken;

import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.token.spi.TokenValidationRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MockJtiValidationRule implements TokenValidationRule {
    private final JtiValidationStore jtiValidationStore;

    public MockJtiValidationRule(JtiValidationStore jtiValidationStore) {
        this.jtiValidationStore = jtiValidationStore;
    }

    @Override
    public Result<Void> checkRule(@NotNull ClaimToken toVerify, @Nullable Map<String, Object> additional) {
        var jti = toVerify.getStringClaim(JwtRegisteredClaimNames.JWT_ID);
        if (jti != null) {
            var entry = jtiValidationStore.findById(jti); // check if existed before
            var res = jtiValidationStore.storeEntry(new JtiValidationEntry(jti));
            if (res.failed()) {
                return Result.failure(res.getFailureDetail());
            }

            if (entry == null) {
                return Result.success();
            }
            return Result.failure("The JWT id '%s' was already used.".formatted(jti));
        }
        return Result.success();
    }
}
